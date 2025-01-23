package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.*;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
import kr.hhplus.be.server.support.aop.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final PointService pointService;

    @Transactional
    public PaymentHttpDto.PaymentCompletedResponse payment(String token, Long reservationId, Long userId) {
        Queue queue = queueService.getToken(token);
        Reservation reservation = reservationService.validateReservation(reservationId, userId);
        Seat seat = concertService.getSeat(reservation.getSeatId());
        Point point = pointService.getPoint(userId);

        point.usePoint(seat.getSeatPrice());
        reservation.changeCompletedStatus();
        queueService.expireToken(queue);

        Payment completedPayment = paymentService.createPayment(reservationId, userId, seat.getSeatPrice());

        return PaymentHttpDto.PaymentCompletedResponse.of(completedPayment.getId(), completedPayment.getAmount(), completedPayment.getPaymentStatus());
    }

    @RedisDistributedLock(key = "'payment userId :' + #userId")
    @Transactional
    public PaymentHttpDto.PaymentCompletedResponse paymentWithDistributedLock(String token, Long reservationId, Long userId) {
        Queue queue = queueService.getToken(token);
        Reservation reservation = reservationService.validateReservationWithoutLock(reservationId, userId);

        log.info("Reservation Status = {}", reservation.getStatus());

        Seat seat = concertService.getSeat(reservation.getSeatId());
        Point point = pointService.getPoint(userId);

        point.usePoint(seat.getSeatPrice());
        reservation.changeCompletedStatus();
        queueService.expireToken(queue);

        Payment completedPayment = paymentService.createPayment(reservationId, userId, seat.getSeatPrice());

        return PaymentHttpDto.PaymentCompletedResponse.of(completedPayment.getId(), completedPayment.getAmount(), completedPayment.getPaymentStatus());
    }

}
