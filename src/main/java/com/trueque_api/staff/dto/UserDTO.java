package com.trueque_api.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDTO {
    private final String email;
    private final String name;
    private final String token;
}