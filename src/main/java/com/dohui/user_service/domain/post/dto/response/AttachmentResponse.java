package com.dohui.user_service.domain.post.dto.response;

import com.dohui.user_service.domain.post.dto.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentResponse {
    private String originalName;
    private String url;

    public static AttachmentResponse from(Attachment attachment){
        return new AttachmentResponse(
                attachment.getOriginalName(),
                attachment.getUrl()
        );
    }
}
