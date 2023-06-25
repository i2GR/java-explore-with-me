package ru.practicum.ewm.app.user.admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.app.dto.UserCommonFullDto;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.app.user.model.UserDtoMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepo;

    private final UserDtoMapper userMapper;

    @Transactional
    @Override
    public UserCommonFullDto addUser(UserCommonFullDto dto) {
        User created = saveOrThrowConflictException(dto);
        log.info("admin:new user added:[email:{}; id:{}]", created.getEmail(), created.getId());
        return userMapper.toFullDto(created);
    }

    @Override
    public List<UserCommonFullDto> getUserListByConditions(Long[] userIds, long from, int size) {
        log.info("admin:receiving user list by paging params: from {} size {}", from, size);
        Pageable page = PageRequest.of((int) (from / size), size);
        List<User> userList;
        if (userIds == null) {
            log.info("admin: finding common user list");
            userList = userRepo.findAllBy(page);
        } else {
            log.info("admin: finding users by list of id {}", Arrays.toString(userIds));
            userList = userRepo.findAllByIdIn(Arrays.asList(userIds), page);
        }
        log.info("received user list of size {}", userList.size());
        return userList.stream()
                .map(userMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        try {
            log.info("deleting user by id {}", id);
            userRepo.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            log.info("user with id={} not found: throwing exception", id);
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
    }

    private User saveOrThrowConflictException(UserCommonFullDto dto) {
        try {
            return userRepo.save(userMapper.fromFullDto(dto));
        } catch (DataIntegrityViolationException dive) {
            throw new ConditionViolationException("user name or email clashes with present other user data");
        }
    }
}