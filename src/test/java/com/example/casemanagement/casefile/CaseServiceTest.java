package com.example.casemanagement.casefile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.casemanagement.audit.AuditAction;
import com.example.casemanagement.casefile.dto.AddCommentRequest;
import com.example.casemanagement.casefile.dto.AuditLogResponse;
import com.example.casemanagement.casefile.dto.CaseSummaryResponse;
import com.example.casemanagement.casefile.dto.CreateCaseRequest;
import com.example.casemanagement.casefile.dto.UpdateStatusRequest;
import com.example.casemanagement.common.InvalidStatusTransitionException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CaseServiceTest {

    @Autowired
    private CaseService caseService;

    @Test
    void createCaseWorks() {
        var created = caseService.createCase(validRequest("Missing document"));

        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("Missing document");
        assertThat(created.status()).isEqualTo(CaseStatus.NEW);
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
        assertThat(created.auditLogs())
                .extracting(AuditLogResponse::action)
                .containsExactly(AuditAction.CASE_CREATED);
    }

    @Test
    void statusCanChangeFromNewToInProgress() {
        var created = caseService.createCase(validRequest("Address update"));

        var updated = caseService.updateStatus(
                created.id(),
                new UpdateStatusRequest(CaseStatus.IN_PROGRESS)
        );

        assertThat(updated.status()).isEqualTo(CaseStatus.IN_PROGRESS);
        assertThat(updated.auditLogs())
                .extracting(AuditLogResponse::action)
                .contains(AuditAction.STATUS_CHANGED);
    }

    @Test
    void statusCannotChangeFromNewDirectlyToClosed() {
        var created = caseService.createCase(validRequest("Appeal"));

        assertThatThrownBy(() -> caseService.updateStatus(
                created.id(),
                new UpdateStatusRequest(CaseStatus.CLOSED)
        ))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("NEW")
                .hasMessageContaining("CLOSED");
    }

    @Test
    void commentReceivesTimestamp() {
        var created = caseService.createCase(validRequest("Question about invoice"));

        var updated = caseService.addComment(
                created.id(),
                new AddCommentRequest("anna", "Customer asked for a copy of the invoice.")
        );

        assertThat(updated.comments()).hasSize(1);
        assertThat(updated.comments().get(0).createdAt()).isNotNull();
        assertThat(updated.auditLogs())
                .extracting(AuditLogResponse::action)
                .contains(AuditAction.COMMENT_ADDED);
    }

    @Test
    void filteringByStatusWorks() {
        var first = caseService.createCase(validRequest("First case"));
        caseService.createCase(validRequest("Second case"));
        caseService.updateStatus(first.id(), new UpdateStatusRequest(CaseStatus.IN_PROGRESS));

        List<CaseSummaryResponse> filtered = caseService.listCases(CaseStatus.IN_PROGRESS, null);

        assertThat(filtered)
                .hasSize(1)
                .extracting(CaseSummaryResponse::id)
                .containsExactly(first.id());
    }

    private CreateCaseRequest validRequest(String title) {
        return new CreateCaseRequest(
                title,
                "A short description of the case.",
                "BENEFITS",
                Priority.HIGH,
                "case.worker@example.com"
        );
    }
}
