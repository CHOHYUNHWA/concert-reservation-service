package kr.hhplus.be.server.interfaces.controller;

import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    /**
     * 토큰 발급
     */
    @PostMapping("/token")
    public ResponseEntity<QueueHttpDto.CreatedTokenResponseDto> issueToken(
            @RequestBody QueueHttpDto.CreateTokenRequestDto createTokenRequestDto) {

        Queue token = queueFacade.createToken(createTokenRequestDto.getUserId());
        QueueHttpDto.CreatedTokenResponseDto createdTokenResponseDto = QueueHttpDto.CreatedTokenResponseDto.of(token);

        return new ResponseEntity<>(createdTokenResponseDto, HttpStatus.CREATED);
    }

    /**
     * 대기열 조회
     */
    @GetMapping("/status")
    public ResponseEntity<QueueHttpDto.QueueStatusResponseDto> getStatus(
            @RequestHeader("Token") String token,
            @RequestParam("userId") Long userId
            ) {

        QueueHttpDto.QueueStatusResponseDto queueRemainingCount = queueFacade.getQueueRemainingCount(token, userId);

        return new ResponseEntity<>(queueRemainingCount, HttpStatus.OK);
    }
}
