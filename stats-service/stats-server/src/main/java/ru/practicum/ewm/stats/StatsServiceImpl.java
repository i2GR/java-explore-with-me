package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.utils.DateTimeDeserializer;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.model.Stats;
import ru.practicum.ewm.stats.model.HitDtoMapper;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.ewm.common.utils.Constants.CHECK_TIME_FILTER;
import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final HitDtoMapper statsMapper;

    private final StatsRepository statsRepository;

    private final DateTimeDeserializer timeDeserializer;


    @Override
    @Transactional
    public EndpointHitResponseDto postStats(EndpointHitDto dto) {
        log.info("save stats hit on {} at {} from {}", dto.getUri(), dto.getTimestamp(), dto.getIp());
        Stats stats = statsRepository.save(statsMapper.fromDto(dto));
        return statsMapper.toDto(stats);
    }

    @Override
    public List<ViewStatsDto> getStats(String strStart, String strEnd, String[] uris, boolean fromIpUnique) {
        log.info("service.getStats between {} and {} on {} unique {}", strStart, strEnd, Arrays.toString(uris), fromIpUnique);
        LocalDateTime start = timeDeserializer.parseOrSetDefaultTime(strStart, MIN_TIME);
        LocalDateTime end = timeDeserializer.parseOrSetDefaultTime(strEnd, MIN_TIME.plusYears(100));
        timeDeserializer.checkPermittedTimeDatesRelation(end, start, 1, CHECK_TIME_FILTER, true);
        List<ViewStatsDto> statsList;
        if (uris == null) {
            log.info("selecting w/o uris param by unique ip: {}", fromIpUnique);
            statsList = fromIpUnique
                    ? statsRepository.findInTimePeriodDistinctIp(start, end)
                    : statsRepository.findInTimePeriod(start, end);
        } else {
            log.info("selecting w. uris {} by unique ip: {}", uris, fromIpUnique);
            statsList = fromIpUnique
                    ? statsRepository.findInTimePeriodDistinctIpInURIs(start, end, uris)
                    : statsRepository.findInTimePeriodInURIs(start, end, uris);
        }
        log.info("selected data: size = {} ", statsList.size());
        return statsList;
    }
}