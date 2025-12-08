package com.trueque_api.staff.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class UserSummaryDTO {
    private UUID id;
    private String name;
    private String profilePicture;
}
