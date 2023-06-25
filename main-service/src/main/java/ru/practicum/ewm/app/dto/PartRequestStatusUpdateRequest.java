package ru.practicum.ewm.app.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PartRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private String status;
}