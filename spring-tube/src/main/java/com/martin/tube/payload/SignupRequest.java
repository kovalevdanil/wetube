package com.martin.tube.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{5,32}$)(?!.*[_.]{2})[^_.].*[^_.]$")
    private String username;

    @NotBlank
    @Size(min = 4, max = 32)
    private String password;
}
