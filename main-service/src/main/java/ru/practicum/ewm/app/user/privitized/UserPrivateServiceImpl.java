package ru.practicum.ewm.app.user.privitized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.app.dto.user.SubscriptionCountedUserDto;
import ru.practicum.ewm.app.dto.user.UserCommonFullDto;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.user.model.SubscriptionCountedUser;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.app.user.model.UserDtoMapper;
import ru.practicum.ewm.common.exception.ConditionViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPrivateServiceImpl implements UserPrivateService {


    private final UserRepository userRepo;

    private final UserDtoMapper userMapper;

    @Override
    public UserCommonFullDto getUser(Long userId) {
        log.info("Private Service Feature: receiving user by id {}", userId);
        return userMapper.toFullDto(userRepo.getUserOrThrowNotFound(userId));
    }

    @Override
    public List<SubscriptionCountedUserDto> getUserListByConditions(Long[] userIds, boolean popular, long from, int size) {
        log.info("Private Service Feature: receiving user list by paging from {} size {} with subscription sorting {}",
                from, size, popular);
        Pageable page = popular
                ? PageRequest.of((int) (from / size), size, Sort.by("subscriptionCount").descending())
                : PageRequest.of((int) (from / size), size);
        List<SubscriptionCountedUser> userList;
        if (userIds == null) {
            log.info("Private Service Feature: finding all user list");
            userList = userRepo.getAllUsersWithSubscriptionCount(page);
        } else {
            log.info("Private Service Feature: finding users by list of id {}", Arrays.toString(userIds));
            userList = userRepo.getUsersByIdsWithSubscriptionCount(Arrays.asList(userIds), page);
        }
        log.info("received user list of size {}", userList.size());
        return userList.stream()
                .map(u -> userMapper.toCountedUser(userMapper.toShortDto(u.getUser()), u.getSubscriptionCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserCommonFullDto changeSubscriptionMode(Long userId, Long subjectId, boolean newStatus) {
        log.info("Private Service Feature: changing subscription mode of user id {} by user id {}", subjectId,   userId);
        if (userId != subjectId) {
            throw new ConditionViolationException("no access to change subscription mode");
        }
        User user = userRepo.getUserOrThrowNotFound(subjectId);
        user.setSubscriptionsAllowed(newStatus);
        return userMapper.toFullDto(user);
    }
}
