package kr.hhplus.be.server.application.intergration;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.QueueJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class QueueFacadeTest {

    private final Logger log = Logger.getLogger(QueueFacadeTest.class.getName());

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueJpaRepository queueJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @Transactional
    void 대기열이_없어_ACTIVE_토큰_발급_성공(){
        //given
        User user = User.builder()
                .name("테스트")
                .build();
        User savedUser = userJpaRepository.save(user);

        //when
        Queue activeToken = queueFacade.createToken(savedUser.getId());

        //then
        assertThat(activeToken).isNotNull();
        assertThat(activeToken.getStatus()).isEqualTo(QueueStatus.ACTIVE);

    }


    @Test
    @Transactional
    void 대기열이_30명_이상_WAITING_토큰_발급_성공(){
        User user = User.builder()
                .name("테스트")
                .build();
        User savedUser = userJpaRepository.save(user);

        //given
        for(int i = 0; i < 30; i++) {
            Queue queue = Queue.builder()
                    .token("test")
                    .createdAt(LocalDateTime.now())
                    .enteredAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .status(QueueStatus.ACTIVE)
                    .build();

            queueRepository.save(queue);
        }

        //when
        Queue token = queueFacade.createToken(savedUser.getId());

        //then
        assertThat(token).isNotNull();
        assertThat(token.getStatus()).isEqualTo(QueueStatus.WAITING);
    }

    @Test
    @Transactional
    void 대기열_조회_성공(){
        //given
        User user = User.builder()
                .name("테스트")
                .build();
        User savedUser = userJpaRepository.save(user);

        Queue token = queueFacade.createToken(savedUser.getId());
        for(int i = 0; i < 30; i++) {
            Queue queue = Queue.builder()
                    .token("test")
                    .createdAt(LocalDateTime.now())
                    .enteredAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .status(QueueStatus.ACTIVE)
                    .build();

            queueRepository.save(queue);
        }

        Queue generatedToken = queueFacade.createToken(savedUser.getId());

        //when
        QueueHttpDto.QueueStatusResponseDto queueRemainingCount = queueFacade.getQueueRemainingCount(generatedToken.getToken(), savedUser.getId());

        //then
        assertThat(queueRemainingCount).isNotNull();
        assertThat(queueRemainingCount.getStatus()).isEqualTo(QueueStatus.WAITING);
        assertThat(queueRemainingCount.getRemainingQueueCount()).isInstanceOf(Long.class);

    }

    @Test
    @Transactional
    void 만료된_토큰으로_대기열_조회시_예외_던지기(){
        //given
        User user = User.builder()
                .name("테스트")
                .build();
        User savedUser = userJpaRepository.save(user);

        Queue token = Queue.builder()
                .token("test")
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .enteredAt(LocalDateTime.now().minusMinutes(5))
                .expiredAt(LocalDateTime.now().minusMinutes(5))
                .status(QueueStatus.EXPIRED)
                .build();

        Queue expiredToken = queueRepository.save(token);

        //when //then
        assertThatThrownBy(() -> queueFacade.getQueueRemainingCount(expiredToken.getToken(), savedUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.INVALID_TOKEN.getMessage());
    }

}
