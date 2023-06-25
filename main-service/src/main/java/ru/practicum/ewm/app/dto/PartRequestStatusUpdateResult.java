package ru.practicum.ewm.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PartRequestStatusUpdateResult {

    private List<PartRequestDto> confirmedRequests;

    private List<PartRequestDto> rejectedRequests;
}