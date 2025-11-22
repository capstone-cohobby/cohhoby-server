package com.backthree.cohobby.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdateImageRequest {
    @Schema(description = "이미지 리스트")
    private List<MultipartFile> images;
}
