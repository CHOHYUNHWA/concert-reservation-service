package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.interfaces.dto.TokenRequestDto;
import kr.hhplus.be.server.interfaces.dto.QueueResponseDto;
import kr.hhplus.be.server.interfaces.dto.TokenResponseDto;
import kr.hhplus.be.server.support.type.QueueStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    /**
     * 토큰 발급
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken(@RequestBody TokenRequestDto tokenRequestDto) {

        return new ResponseEntity<>(
                new TokenResponseDto(
                        "7b3366bc-6c19-41d8-a97f-6ac312479aa9",
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                HttpStatus.CREATED);
    }

    /**
     * 대기열 조회
     */
    @GetMapping("/status")
    public ResponseEntity<QueueResponseDto> getStatus(
            @RequestHeader("Token") String token,
            @RequestParam("userId") Long userId
            ) {
        return new ResponseEntity<>(
                new QueueResponseDto(
                        QueueStatus.WAITING,
                        10L),
                HttpStatus.OK);
    }
}
