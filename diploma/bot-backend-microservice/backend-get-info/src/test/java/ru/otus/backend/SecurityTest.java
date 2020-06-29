//package ru.otus.backend;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import ru.otus.configurations.SecurityConfiguration;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@DisplayName("Тест проверяет:")
//@WebMvcTest(SecurityConfiguration.class)
////@SpringBootTest
//class SecurityTest {
//
//    @MockBean
//    TestRestTemplate restTemplate;
//
//
//    @Autowired
//    MockMvc mvc;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//   // private final String passwordCorrect = passwordEncoder.encode("11111");
//
//    @Test
//    @DisplayName("Перенаправление на страницу аутентификации при запросе закрытых ресурсов:")
//    void shouldRedirectedToLoginPage() throws Exception {
//        mvc.perform(get("/actuator"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("**/login"));
//    }
//
//    @DisplayName("доступ закрытых ресурсов для ADMIN")
//    @Test
//    @WithMockUser  (username="admin123",authorities={"ADMIN"},password="11111")
//    void showUsersOnlyForAdmin() throws Exception {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//        mvc.perform(get("/users"))
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("доступ для USER только к начальной странице")
//    @Test
//    @WithMockUser  (username="admin123",authorities={"USER"},password="11111")
//    void showOnlyIndexForUser2() throws Exception {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//        mvc.perform(get("/actuator"))
//                .andExpect(status().isForbidden());
//        mvc.perform(get("/hystrix"))
//                .andExpect(status().isForbidden());
//    }
//
//    @DisplayName("доступ для USER только к начальной странице")
//    @Test
//    @WithMockUser  (username="admin")//,authorities={"ADMIN"},password="11111")
//    void showOnlyIndexForUser23() throws Exception {
////        mvc = MockMvcBuilders
////                .webAppContextSetup(context)
////                .apply(springSecurity())
////                .build();
//        mvc.perform(get("/actuator").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//        mvc.perform(get("/actuator"))
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("доступ для USER только к начальной странице")
//    @Test
//    @WithMockUser  (username="admin",authorities={"ADMIN"}, password = "11111")
//    void showOnlyIndexForUser3() throws Exception {
////        ResponseEntity<String> result = restTemplate.withBasicAuth("admin",passwordEncoder.encode("11111"))//passwordCorrect)
////                .getForEntity("/actuator", String.class);
////        assertThat(HttpStatus.OK).isEqualTo(result.getStatusCode());
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//
//        mvc.perform(get("/actuator"))
//                .andExpect(status().isOk());
////        mvc.perform(get("/hysrtix"))
////                .andExpect(status().isOk());
//    }
//
//
//    @DisplayName("доступ для USER только к начальной странице")
//    @Test
//    @WithMockUser  (username="admin123",authorities={"USER"},password="11111")
//    void showOnlyIndexForUser() throws Exception {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//        mvc.perform(get("/users"))
//                .andExpect(status().isForbidden());
//        mvc.perform(get("/create"))
//                .andExpect(status().isForbidden());
//        mvc.perform(get("/"))
//                .andExpect(status().isOk());
//    }
//}