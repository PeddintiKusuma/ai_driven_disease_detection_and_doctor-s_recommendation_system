package com.aihealthcare.controller;

import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.AuditLogResponse;
import com.aihealthcare.dto.response.DocumentResponse;
import com.aihealthcare.dto.response.MedicalHistoryEntry;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.AuditLogService;
import com.aihealthcare.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final AuditLogService auditLogService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/api/documents")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocuments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(documentService.getUserDocuments(user.getId())));
    }

    @GetMapping("/api/users/medical-history")
    public ResponseEntity<ApiResponse<List<MedicalHistoryEntry>>> getMedicalHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(documentService.getMedicalHistory(user.getId())));
    }

    @GetMapping("/api/admin/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.getAllLogs()));
    }
}
