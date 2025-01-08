package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.infra.repository.jpa.PointJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.Point.PointHttpDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class PointFacadeTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;


    private Long savedUserId;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("테스트").build();

        User savedUser = userJpaRepository.save(user);
        savedUserId = savedUser.getId();
    }


    @Test
    @Transactional
    void 유저의_잔액을_충전(){
        //given
        Point point = Point.builder()
                .userId(savedUserId)
                .amount(0L)
                .updatedAt(LocalDateTime.now())
                .build();
        Point savedPoint = pointJpaRepository.save(point);

        Long chargeAmount = 1000L;
        Long expectedAmount = 1000L;
        //when
        PointHttpDto.ChargePointResponseDto chargePointResponseDto = pointFacade.chargePoint(savedUserId, chargeAmount);

        //then
        assertThat(chargePointResponseDto).isNotNull();
        assertThat(chargePointResponseDto.getUserId()).isEqualTo(savedUserId);
        assertThat(chargePointResponseDto.getCurrentPoint()).isEqualTo(expectedAmount);

    }


    @Test
    @Transactional
    void 유저의_잔액을_조회(){
        //given
        Point point = Point.builder()
                .userId(savedUserId)
                .amount(0L)
                .updatedAt(LocalDateTime.now())
                .build();
        Point savedPoint = pointJpaRepository.save(point);


        //when
        PointHttpDto.GetPointResponseDto getPointResponseDto = pointFacade.getPoint(savedUserId);

        //then
        assertThat(getPointResponseDto).isNotNull();
        assertThat(getPointResponseDto.getUserId()).isEqualTo(savedUserId);
        assertThat(getPointResponseDto.getCurrentAmount()).isEqualTo(savedPoint.getAmount());

    }
}
