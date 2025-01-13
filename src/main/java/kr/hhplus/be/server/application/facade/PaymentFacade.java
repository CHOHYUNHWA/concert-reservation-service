package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.*;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final PointService pointService;

    @Transactional
    public PaymentHttpDto.PaymentCompletedResponse payment(String token, Long reservationId, Long userId) {
        Queue queue = queueService.validateToken(token);
        Reservation reservation = reservationService.validateReservation(reservationId, userId);
        Seat seat = concertService.getSeat(reservation.getSeatId());
        Point point = pointService.getPoint(userId);

        point.usePoint(seat.getSeatPrice());
        reservation.changeCompletedStatus();
        queueService.expireToken(queue);

        Payment completedPayment = paymentService.createPayment(reservationId, userId, seat.getSeatPrice());

        return PaymentHttpDto.PaymentCompletedResponse.of(completedPayment.getId(), completedPayment.getAmount(), completedPayment.getPaymentStatus());
    }
}
