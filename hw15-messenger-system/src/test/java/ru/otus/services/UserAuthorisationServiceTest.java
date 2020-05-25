package ru.otus.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.otus.controllers.UsersController;
import ru.otus.front.FrontendService;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тест проверяет:")
@WebMvcTest(UsersController.class)
class UsersControllerTest {

    @MockBean
    private FrontendService frontendService;
    @MockBean
    TestRestTemplate restTemplate;

    @MockBean
    private UserAuthorisationService authorisationService;

    @MockBean
    private SimpMessagingTemplate template;

    @Autowired
    MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Test
    @DisplayName("Перенаправление на страницу аутентификации при запросе закрытых ресурсов:")
    void shouldRedirectedToLoginPage() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("доступ закрытых ресурсов для ADMIN")
    @Test
    @WithMockUser  (username="admin123",authorities={"ADMIN"},password="11111")
    void showUsersOnlyForAdmin() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @DisplayName("доступ для USER только к начальной странице")
    @Test
    @WithMockUser  (username="admin123",authorities={"USER"},password="11111")
    void showOnlyIndexForUser() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        mvc.perform(get("/users"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/create"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}