package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private static final long MAX_ACTIVE_TOKENS = 30;

    public Queue getToken(String token){
        return queueRepository.findToken(token);
    }

    public Long getWaitingTokenCount(String token){
        return queueRepository.getWaitingRank(token);
    }

    @Transactional
    public Queue createToken(){
        Long activeCount = queueRepository.getActiveTokenCount();
        Queue token = Queue.createToken(activeCount);

        if(token.checkStatus()){
            queueRepository.saveActiveToken(token.getToken());
        } else {
            queueRepository.saveWaitingToken(token.getToken());
        }
        return token;
    }

    public void expireToken(Queue token) {
        queueRepository.removeActiveToken(token.getToken());
    }

    public void validateToken(String token){
        boolean exists = queueRepository.activeTokenExist(token);
        if(!exists){
            throw new CustomException(ErrorType.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
        }
    }

    public void updateActiveToken(){
        long activeCount = queueRepository.getActiveTokenCount();
        if(activeCount < MAX_ACTIVE_TOKENS){
            long neededTokens = MAX_ACTIVE_TOKENS - activeCount;
            List<String> waitingTokens = queueRepository.retrieveAndRemoveWaitingToken(neededTokens);
            waitingTokens.forEach(queueRepository::saveActiveToken);
        }
    }

    public void removeExpiredTokens() {
        queueRepository.removeExpiredTokens();
    }
}
