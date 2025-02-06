package kr.hhplus.be.server.application.integration;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
public class QueueFacadeTest {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockitoSpyBean
    private QueueRepository queueRepository;
    @Autowired
    private QueueService queueService;

    @Test
    void 토큰을_생성한다() {
        User user = User.builder()
                .name("이름")
                .build();

        User savedUser = userJpaRepository.save(user);

        Queue token = queueFacade.createToken(savedUser.getId());

        assertThat(token).isNotNull();
        assertThat(token.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }
}
