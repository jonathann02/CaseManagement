package com.example.casemanagement.casefile;

import com.example.casemanagement.audit.AuditAction;
import com.example.casemanagement.audit.AuditLog;
import com.example.casemanagement.casefile.dto.AddCommentRequest;
import com.example.casemanagement.casefile.dto.AuditLogResponse;
import com.example.casemanagement.casefile.dto.CaseDetailResponse;
import com.example.casemanagement.casefile.dto.CaseSummaryResponse;
import com.example.casemanagement.casefile.dto.CommentResponse;
import com.example.casemanagement.casefile.dto.CreateCaseRequest;
import com.example.casemanagement.casefile.dto.UpdateStatusRequest;
import com.example.casemanagement.comment.CaseComment;
import com.example.casemanagement.common.InvalidStatusTransitionException;
import com.example.casemanagement.common.NotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseService {

    private static final Map<CaseStatus, Set<CaseStatus>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            CaseStatus.NEW, Set.of(CaseStatus.IN_PROGRESS),
            CaseStatus.IN_PROGRESS, Set.of(CaseStatus.WAITING_FOR_CUSTOMER, CaseStatus.RESOLVED),
            CaseStatus.WAITING_FOR_CUSTOMER, Set.of(CaseStatus.IN_PROGRESS, CaseStatus.RESOLVED),
            CaseStatus.RESOLVED, Set.of(CaseStatus.IN_PROGRESS, CaseStatus.CLOSED),
            CaseStatus.CLOSED, Set.of()
    );

    private final CaseRepository caseRepository;
    private final Clock clock;

    public CaseService(CaseRepository caseRepository, Clock clock) {
        this.caseRepository = caseRepository;
        this.clock = clock;
    }

    @Transactional
    public CaseDetailResponse createCase(CreateCaseRequest request) {
        Instant now = Instant.now(clock);
        CaseEntity caseEntity = new CaseEntity(
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                CaseStatus.NEW,
                request.assignedTo(),
                now,
                now
        );
        caseEntity.addAuditLog(new AuditLog(AuditAction.CASE_CREATED, "Case created", now));

        return toDetailResponse(caseRepository.saveAndFlush(caseEntity));
    }

    @Transactional(readOnly = true)
    public List<CaseSummaryResponse> listCases(CaseStatus status, Priority priority) {
        List<CaseEntity> cases;
        if (status != null && priority != null) {
            cases = caseRepository.findByStatusAndPriorityOrderByUpdatedAtDesc(status, priority);
        } else if (status != null) {
            cases = caseRepository.findByStatusOrderByUpdatedAtDesc(status);
        } else if (priority != null) {
            cases = caseRepository.findByPriorityOrderByUpdatedAtDesc(priority);
        } else {
            cases = caseRepository.findAllByOrderByUpdatedAtDesc();
        }

        return cases.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseDetailResponse getCase(long id) {
        return toDetailResponse(findCase(id));
    }

    @Transactional
    public CaseDetailResponse updateStatus(long id, UpdateStatusRequest request) {
        CaseEntity caseEntity = findCase(id);
        CaseStatus currentStatus = caseEntity.getStatus();
        CaseStatus nextStatus = request.status();

        if (currentStatus == nextStatus) {
            return toDetailResponse(caseEntity);
        }
        if (!ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(nextStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot change case status from " + currentStatus + " to " + nextStatus
            );
        }

        Instant now = Instant.now(clock);
        caseEntity.setStatus(nextStatus);
        caseEntity.setUpdatedAt(now);
        caseEntity.addAuditLog(new AuditLog(
                AuditAction.STATUS_CHANGED,
                "Status changed from " + currentStatus + " to " + nextStatus,
                now
        ));

        return toDetailResponse(caseRepository.saveAndFlush(caseEntity));
    }

    @Transactional
    public CaseDetailResponse addComment(long id, AddCommentRequest request) {
        CaseEntity caseEntity = findCase(id);
        Instant now = Instant.now(clock);

        caseEntity.addComment(new CaseComment(request.author(), request.text(), now));
        caseEntity.setUpdatedAt(now);
        caseEntity.addAuditLog(new AuditLog(AuditAction.COMMENT_ADDED, "Comment added by " + request.author(), now));

        return toDetailResponse(caseRepository.saveAndFlush(caseEntity));
    }

    private CaseEntity findCase(long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Case with id " + id + " was not found"));
    }

    private CaseSummaryResponse toSummaryResponse(CaseEntity caseEntity) {
        return new CaseSummaryResponse(
                caseEntity.getId(),
                caseEntity.getTitle(),
                caseEntity.getCategory(),
                caseEntity.getPriority(),
                caseEntity.getStatus(),
                caseEntity.getCreatedAt(),
                caseEntity.getUpdatedAt(),
                caseEntity.getAssignedTo()
        );
    }

    private CaseDetailResponse toDetailResponse(CaseEntity caseEntity) {
        return new CaseDetailResponse(
                caseEntity.getId(),
                caseEntity.getTitle(),
                caseEntity.getDescription(),
                caseEntity.getCategory(),
                caseEntity.getPriority(),
                caseEntity.getStatus(),
                caseEntity.getCreatedAt(),
                caseEntity.getUpdatedAt(),
                caseEntity.getAssignedTo(),
                caseEntity.getComments().stream()
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getAuthor(),
                                comment.getText(),
                                comment.getCreatedAt()
                        ))
                        .toList(),
                caseEntity.getAuditLogs().stream()
                        .map(auditLog -> new AuditLogResponse(
                                auditLog.getId(),
                                auditLog.getAction(),
                                auditLog.getMessage(),
                                auditLog.getCreatedAt()
                        ))
                        .toList()
        );
    }
}
