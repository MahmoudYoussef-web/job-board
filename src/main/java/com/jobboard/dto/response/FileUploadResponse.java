package com.jobboard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {

    private String fileName;
    private String filePath;
    private long sizeBytes;
    private String message;
}
