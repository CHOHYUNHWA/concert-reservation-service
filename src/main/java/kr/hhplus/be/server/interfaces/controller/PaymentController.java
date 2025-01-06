package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.PaymentCompletedResponse;
import kr.hhplus.be.server.interfaces.dto.PaymentRequestDto;
import kr.hhplus.be.server.support.type.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    /**
     * 예매 콘서트 결제
     */
    @PostMapping
    public ResponseEntity<PaymentCompletedResponse> proceedPayment(
            @RequestHeader("Token") String token,
            @RequestBody PaymentRequestDto paymentRequestDto
            ){
        return new ResponseEntity<>(new PaymentCompletedResponse(1L, 100000L, PaymentStatus.COMPLETED), HttpStatus.CREATED);
    }
}
