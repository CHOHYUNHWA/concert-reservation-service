package kr.hhplus.be.server.interfaces.scheduler;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class TokenScheduler {

    private final QueueRepository queueRepository;

    //만료 토큰 상태 변경
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void expireTokens(){
        LocalDateTime now = LocalDateTime.now();
        List<Queue> expiredTokens = queueRepository.findExpiredTokens(now, QueueStatus.ACTIVE);

        for (Queue token : expiredTokens) {
            token.expiredToken();
            queueRepository.save(token);
        }
    }

    public void changeActiveTokens(){
        Long activeTokenCount = queueRepository.countByStatus(QueueStatus.ACTIVE);

        if(activeTokenCount < 30){
            long forChangeTokenCount = 30 - activeTokenCount;
            List<Queue> waitingTokens = queueRepository.findWaitingTokens(forChangeTokenCount);

            for (Queue token : waitingTokens) {
                token.activate();
                queueRepository.save(token);
            }
        }
    }
}
