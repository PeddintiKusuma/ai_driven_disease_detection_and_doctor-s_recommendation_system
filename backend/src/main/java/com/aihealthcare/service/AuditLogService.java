package com.aihealthcare.service;

import com.aihealthcare.dto.response.AuditLogResponse;
import com.aihealthcare.model.entity.AuditLog;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.repository.AuditLogRepository;
import com.aihealthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void log(Long userId, String action, String entityType, Long entityId, String details) {
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build());
    }

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        String userName = "System";
        if (log.getUserId() != null) {
            userName = userRepository.findById(log.getUserId())
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .orElse("Unknown");
        }
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userName(userName)
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
