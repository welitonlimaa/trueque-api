package com.trueque_api.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserDataResponseDTO {
    private final UUID id;
    private final String email;
    private final String name;
    private final String phone;
    private final String googleId;
    private final String profilePicture;
    private final String city;
    private final String state;
}