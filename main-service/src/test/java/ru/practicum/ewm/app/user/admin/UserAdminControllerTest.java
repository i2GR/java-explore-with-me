package ru.practicum.ewm.app.user.admin;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.practicum.ewm.app.TestUtil;
import ru.practicum.ewm.app.dto.UserCommonFullDto;
import ru.practicum.ewm.app.user.model.User;

@WebMvcTest(controllers = UserAdminController.class)
class UserAdminControllerTest {

    private static final String PATH = "/admin/users";


    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    private final User user1 = TestUtil.testUser1();

    private UserCommonFullDto userCommonFullDto;

    @Test
    @DisplayName("POST /admin/users             UserFullDto w/o name >> status 400")
    void postUser_whenNoName_thenStatusBadRequest() throws Exception {
        //given
        userCommonFullDto = UserCommonFullDto.builder().email(user1.getEmail()).build();
        //when
        mvc.perform(post(PATH)
                        .content(objectMapper.writeValueAsString(userCommonFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }
}