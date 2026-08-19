package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private String type;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String downloadUrl;
}
