package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.domain.service.UserService;
import kr.hhplus.be.server.interfaces.dto.queue.QueueHttpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueFacade {

    private final QueueService queueService;
    private final UserService userService;

    @Transactional(readOnly = false)
    public Queue createToken(Long userId){
        userService.existsUser(userId);
        return queueService.createToken();
    }

    public QueueHttpDto.QueueStatusResponseDto getQueueRemainingCount(String tokenString, Long userId){
        userService.existsUser(userId);

        Queue findToken = queueService.getToken(tokenString);
        Long remainingQueueCount  = queueService.checkRemainingQueueCount(findToken);

        return QueueHttpDto.QueueStatusResponseDto.of(findToken.getStatus(), remainingQueueCount);
    }

}
