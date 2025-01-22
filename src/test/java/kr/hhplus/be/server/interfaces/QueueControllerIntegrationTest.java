package kr.hhplus.be.server.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.infra.repository.jpa.QueueJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import kr.hhplus.be.server.support.type.QueueStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QueueControllerIntegrationTest {

    private User user;
    private Queue queue;
    private Queue expiredQueue;


    @Autowired
    private MockMvc mvc;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private QueueJpaRepository queueJpaRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        user = User.builder()
                .name("유저")
                .build();

        userJpaRepository.save(user);

        queue = Queue.builder()
                .token("valid-token")
                .createdAt(LocalDateTime.now())
                .enteredAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .status(QueueStatus.ACTIVE)
                .build();

        queueJpaRepository.save(queue);

        expiredQueue = Queue
                .builder()
                .token("token")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .enteredAt(LocalDateTime.now().minusMinutes(10))
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .status(QueueStatus.EXPIRED)
                .build();

        queueJpaRepository.save(expiredQueue);
    }

    @Test
    void 정상적으로_토큰_발급_성공() throws Exception {
        //given
        QueueHttpDto.CreateTokenRequestDto request = QueueHttpDto.CreateTokenRequestDto
                .builder()
                .userId(user.getId())
                .build();

        //when //then
        mvc.perform(post("/api/queues/token", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void 없는_유저의_경우_토큰_발급_실패() throws Exception {
        //given
        Long notExistUserId = 2L;
        QueueHttpDto.CreateTokenRequestDto request = QueueHttpDto.CreateTokenRequestDto
                .builder()
                .userId(notExistUserId)
                .build();

        //when then
        //when //then
        mvc.perform(post("/api/queues/token", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
    }

    @Test
    void 정상적으로_대기열_조회_성공() throws Exception {
        //given

        //when then
        mvc.perform(get("/api/queues/status")
                        .header("Token", queue.getToken())
                        .param("userId", String.valueOf(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(QueueStatus.ACTIVE.name()))
                .andExpect(jsonPath("$.remainingQueueCount").isNumber());
    }

    @Test
    void 없는_유저의_경우_대기열_조회_실패() throws Exception {
        //given
        Long notExistUserId = 2L;

        //when then
        mvc.perform(get("/api/queues/status")
                        .header("Token", queue.getToken())
                        .param("userId", String.valueOf(notExistUserId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
    }

    @Test
    void 유효하지_않는_토큰의_경우_대기열_조회_실패() throws Exception {
        //given
        String invalidToken = "invalid-token";

        //when then
        mvc.perform(get("/api/queues/status")
                        .header("Token", invalidToken)
                        .param("userId", String.valueOf(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("없는 토큰 입니다."));
    }

    @Test
    void 만료된_토큰의_경우_대기열_조회_실패() throws Exception {
        //given

        //when then
        mvc.perform(get("/api/queues/status")
                        .header("Token", expiredQueue.getToken())
                        .param("userId", String.valueOf(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않는 토큰 입니다."));
    }

}
