package ru.practicum.ewm.app.user.privitized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public SubscriptionCountedUserDto getUser(Long userId, Long anotherUserId) {
        log.info("Private Service Feature: receiving user by id {}", anotherUserId);
        //проверка, что запрашивающий пользователь существует
        userRepo.getUserOrThrowNotFound(userId);
        return userMapper.toCountedUserDto(userRepo.getUserWithSubscriptionsCountOrThrowNotFound(anotherUserId));
    }

    @Override
    public List<SubscriptionCountedUserDto> getUserListByConditions(Long userId,
                                                                    Long[] userIds,
                                                                    long from,
                                                                    int size) {
        log.info("Private Service Feature: receiving user list by paging from {} size {}", from, size);
        userRepo.getUserOrThrowNotFound(userId);
        Pageable page = PageRequest.of((int) (from / size), size);
        List<SubscriptionCountedUser> userList;
        if (userIds == null) {
            log.info("Private Service Feature: finding all user list");
            userList = userRepo.getAllUsersWithSubscriptionCount(page);
        } else {
            log.info("Private Service Feature: finding users by list of id {}", Arrays.toString(userIds));
            userList = userRepo.getAllUsersByIdsWithSubscriptionCount(Arrays.asList(userIds), page);
        }
        log.info("received user list of size {}", userList.size());
        return userList.stream()
                .map(userMapper::toCountedUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserCommonFullDto changeSubscriptionMode(Long userId, Long subjectId, boolean newStatus) {
        log.info("Private Service Feature: changing subscription mode of user id {} by user id {}", subjectId,   userId);
        if (!userId.equals(subjectId)) {
            throw new ConditionViolationException("no access to change subscription mode");
        }
        User user = userRepo.getUserOrThrowNotFound(subjectId);
        user.setSubscriptionsAllowed(newStatus);
        return userMapper.toFullDto(user);
    }
}
