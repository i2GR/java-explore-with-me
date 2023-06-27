package ru.practicum.ewm.app.user.privitized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.app.dto.user.SubscriptionCountedUserDto;
import ru.practicum.ewm.app.dto.user.UserCommonFullDto;
import ru.practicum.ewm.app.user.admin.UserAdminService;
import ru.practicum.ewm.common.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserPrivateController {

    private final UserPrivateService userPrivateService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserCommonFullDto> getUser(@PathVariable(name = "userId") Long userId) {
        log.info("Private API (feature): get user by id {}", userId);
        return new ResponseEntity<>(userPrivateService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionCountedUserDto>> getUserListByConditions(
            @RequestParam(name = "ids", required = false) Long[] ids,
            @RequestParam(name = "popular", defaultValue = "false") boolean popular,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Private API (feature) :get users of ids {} from {} with size {}", ids, from, size);
        return new ResponseEntity<>(userPrivateService.getUserListByConditions(ids, popular, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/submode/{subjectId}")
    public ResponseEntity<UserCommonFullDto> changeSubscriptionMode(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(name = "status", defaultValue = "false") boolean newStatus) {
        log.info("Private API (feature) : change subscription mode of user {}", subjectId);
        return new ResponseEntity<>(userPrivateService.changeSubscriptionMode(userId, subjectId, newStatus), HttpStatus.OK);
    }
}