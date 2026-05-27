package ru.job4j.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithApprovedInterviewsDTO {
    private ProfileDTO profileDTO;
    private long approvedInterviews;
}
