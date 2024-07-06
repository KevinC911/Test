package com.uca.project.domain.DTOs.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDTO {
    private UUID code;
    private String numHome;
    private List<String> representatives;

}
