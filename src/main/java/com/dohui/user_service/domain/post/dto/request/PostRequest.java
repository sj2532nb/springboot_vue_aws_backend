package com.dohui.user_service.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequest {
    private String title;
    private String content;

    private List<String> fileNames;
    private List<String> fileUrls;

    @JsonProperty("isPrivate")
    private boolean isPrivate = false;
}
