package ru.practicum.ewm.app.user.admin;

import java.util.List;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.ewm.app.dto.user.UserCommonFullDto;
import ru.practicum.ewm.common.validation.OnCreate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PostMapping
    public ResponseEntity<UserCommonFullDto> addUser(
            @RequestBody @Validated(value = OnCreate.class) UserCommonFullDto dto) {
        log.info("Admin API:post user");
        return new ResponseEntity<>(userAdminService.addUser(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserCommonFullDto>> getUser(
            @RequestParam(name = "ids", required = false) Long[] ids,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Admin API:get users of ids {} from {} with size {}", ids, from, size);
        return new ResponseEntity<>(userAdminService.getUserListByConditions(ids, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        log.info("Admin API: delete user with id {}", userId);
        userAdminService.deleteUserById(userId);
    }
}