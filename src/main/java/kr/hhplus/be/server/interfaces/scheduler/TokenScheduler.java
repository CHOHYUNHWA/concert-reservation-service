package kr.hhplus.be.server.interfaces.scheduler;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.service.QueueService;
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

   private final QueueService queueService;

   //5초 마다 갱신
    @Scheduled(fixedDelay = 5000)
    public void updateActiveToken(){
        queueService.updateActiveToken();
    }
}
