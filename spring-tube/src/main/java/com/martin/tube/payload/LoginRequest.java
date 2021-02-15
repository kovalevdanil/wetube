package com.martin.tube.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 32)
    private String password;
}
