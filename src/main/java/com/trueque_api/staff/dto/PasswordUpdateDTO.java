package com.trueque_api.staff.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
}
