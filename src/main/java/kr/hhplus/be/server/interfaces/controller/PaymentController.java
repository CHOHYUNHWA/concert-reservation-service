package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
import kr.hhplus.be.server.support.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    /**
     * 예매 콘서트 결제
     */
    @PostMapping
    public ResponseEntity<PaymentHttpDto.PaymentCompletedResponse> proceedPayment(
            @RequestHeader("Token") String token,
            @RequestBody PaymentHttpDto.PaymentRequestDto paymentRequestDto
            ){

        PaymentHttpDto.PaymentCompletedResponse payment = paymentFacade.payment(token, paymentRequestDto.getReservationId(), paymentRequestDto.getUserId());

        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

}
