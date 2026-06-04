package com.example.casemanagement.casefile;

import com.example.casemanagement.casefile.dto.AddCommentRequest;
import com.example.casemanagement.casefile.dto.CaseDetailResponse;
import com.example.casemanagement.casefile.dto.CaseSummaryResponse;
import com.example.casemanagement.casefile.dto.CreateCaseRequest;
import com.example.casemanagement.casefile.dto.UpdateStatusRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<CaseDetailResponse> createCase(@Valid @RequestBody CreateCaseRequest request) {
        CaseDetailResponse response = caseService.createCase(request);
        return ResponseEntity
                .created(URI.create("/api/cases/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<CaseSummaryResponse> listCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) Priority priority
    ) {
        return caseService.listCases(status, priority);
    }

    @GetMapping("/{id}")
    public CaseDetailResponse getCase(@PathVariable long id) {
        return caseService.getCase(id);
    }

    @PatchMapping("/{id}/status")
    public CaseDetailResponse updateStatus(
            @PathVariable long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return caseService.updateStatus(id, request);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CaseDetailResponse> addComment(
            @PathVariable long id,
            @Valid @RequestBody AddCommentRequest request
    ) {
        CaseDetailResponse response = caseService.addComment(id, request);
        return ResponseEntity
                .created(URI.create("/api/cases/" + id + "/comments"))
                .body(response);
    }
}
