package ru.checkdev.mock.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.checkdev.mock.domain.Interview;
import ru.checkdev.mock.domain.Wisher;
import ru.checkdev.mock.dto.InterviewDTO;
import ru.checkdev.mock.dto.WisherDto;

@Component
@AllArgsConstructor
public class WisherMapper {

    public Wisher getWisher(WisherDto wisherDto, Interview interview) {
        return Wisher.of()
                .interview(interview)
                .userId(wisherDto.getUserId())
                .contactBy(wisherDto.getContactBy())
                .approve(wisherDto.isApprove())
                .build();
    }

    public Wisher getWisher(WisherDto wisherDto, InterviewDTO interviewDTO) {
        Interview interview = InterviewMapper.getInterview(interviewDTO);
        return Wisher.of()
                .interview(interview)
                .userId(wisherDto.getUserId())
                .contactBy(wisherDto.getContactBy())
                .approve(wisherDto.isApprove())
                .build();
    }
}
