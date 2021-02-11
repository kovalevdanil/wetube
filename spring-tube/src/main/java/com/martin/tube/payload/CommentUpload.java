package com.martin.tube.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentUpload {
    @NotBlank
    private String content;

}
