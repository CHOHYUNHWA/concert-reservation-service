package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    public Queue getToken(String token){
        return queueRepository.findQueue(token);
    }

    @Transactional
    public Queue createToken(){
        Long activeCount = queueRepository.countByStatus(QueueStatus.ACTIVE);
        Queue token = Queue.createToken(activeCount);
        return queueRepository.save(token);
    }

    public void expireToken(Queue token) {
        token.expiredToken();
        queueRepository.save(token);
    }

    public boolean checkQueueStatus(Queue queue) {
        return queue.checkStatus();
    }

    public Long checkRemainingQueueCount(Queue queue){
        Long latestQueueId =  queueRepository.findLatestActiveQueueIdByStatus(queue.getId());
        return queue.getId() - latestQueueId;
    }

    public Queue validateToken(String token){
        Queue queue = queueRepository.findQueue(token);
        queue.validateToken();
        return queue;
    }
}
