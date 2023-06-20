package ru.practicum.ewm.app.user.admin;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;

import ru.practicum.ewm.app.TestUtil;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.dto.UserCommonFullDto;
import ru.practicum.ewm.app.user.model.User;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserAdminServiceImplMockTest {

    @InjectMocks
    private final UserAdminServiceImpl service;

    @MockBean
    private final UserRepository userRepo;

    private final User user = TestUtil.testUser1();

    private UserCommonFullDto inputDto;
    private UserCommonFullDto responseDto;

    @Test
    void addUser_whenDataOk_thenOk() {
        //given
        inputDto = UserCommonFullDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        Mockito.when(userRepo.save(any())).thenReturn(user);
        //when
        responseDto = service.addUser(inputDto);
        //then
        assertThat(responseDto).isNotNull();
        assertEquals(user.getName(), responseDto.getName());
        assertEquals(user.getEmail(), responseDto.getEmail());
    }
}