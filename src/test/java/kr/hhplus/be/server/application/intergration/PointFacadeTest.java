package kr.hhplus.be.server.application.intergration;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.domain.entity.Point;
import kr.hhplus.be.server.domain.entity.User;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.infra.repository.jpa.PointJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.point.PointHttpDto;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class PointFacadeTest {

    private User user;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointService pointService;


    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        user = User.builder()
                .name("테스트").build();

        userJpaRepository.save(user);

        Point point = Point.builder()
                .userId(user.getId())
                .amount(0L)
                .updatedAt(LocalDateTime.now())
                .build();
        pointJpaRepository.save(point);

    }


    @Test
    void 유저의_잔액을_충전(){
        //given
        Long chargeAmount = 1000L;
        Long expectedAmount = 1000L;
        //when
        PointHttpDto.ChargePointResponseDto chargePointResponseDto = pointFacade.chargePoint(user.getId(), chargeAmount);

        //then
        assertThat(chargePointResponseDto).isNotNull();
        assertThat(chargePointResponseDto.getUserId()).isEqualTo(user.getId());
        assertThat(chargePointResponseDto.getCurrentPoint()).isEqualTo(expectedAmount);

    }


    @Test
    void 유저의_잔액을_조회(){
        //given
        Point chargedPoint = pointService.chargePoint(user.getId(), 1000L);


        //when
        PointHttpDto.GetPointResponseDto getPointResponseDto = pointFacade.getPoint(user.getId());

        //then
        assertThat(getPointResponseDto).isNotNull();
        assertThat(getPointResponseDto.getCurrentAmount()).isEqualTo(chargedPoint.getAmount());
    }
}
