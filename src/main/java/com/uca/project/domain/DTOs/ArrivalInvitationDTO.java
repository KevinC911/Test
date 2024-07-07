package com.uca.project.domain.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArrivalInvitationDTO {
    private String home;
    private LocalDateTime arrivalTime;
}
