package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACTIVE_TOKEN_KEY = "activeToken";
    private static final String WAITING_TOKEN_KEY = "waitingToken";
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    @Override
    public boolean activeTokenExist(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_TOKEN_KEY, token)); // ✅ Set에 존재하는지 확인 (SISMEMBER)
    }

    @Override
    public void removeActiveToken(String token) {
        redisTemplate.opsForSet().remove(ACTIVE_TOKEN_KEY, token);
    }

    @Override
    public Long getActiveTokenCount() {
        return redisTemplate.opsForSet().size(ACTIVE_TOKEN_KEY);
    }

    @Override
    public void saveActiveToken(String token) {
        redisTemplate.opsForSet().add(ACTIVE_TOKEN_KEY, token);
    }

    @Override
    public Long getWaitingTokenCount() {
        return redisTemplate.opsForZSet().zCard(WAITING_TOKEN_KEY);
    }


    @Override
    public void saveWaitingToken(String token) {
        redisTemplate.opsForZSet().add(WAITING_TOKEN_KEY, token, System.currentTimeMillis());
    }

    @Override
    public List<String> retrieveAndRemoveWaitingToken(long count) {

        Set<String> tokens = redisTemplate.opsForZSet().range(WAITING_TOKEN_KEY, 0, count - 1);
        if(tokens != null && !tokens.isEmpty()) {
            redisTemplate.opsForZSet().remove(WAITING_TOKEN_KEY, tokens.toArray());
            return tokens.stream().toList();
        }
        return List.of();
    }

    @Override
    public Queue findToken(String token) {
        boolean isActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_TOKEN_KEY, token));

        if(isActive) {
            return Queue.builder()
                    .token(token)
                    .status(QueueStatus.ACTIVE)
                    .build();
        }

        Long waitingRank = redisTemplate.opsForZSet().rank(WAITING_TOKEN_KEY, token);
        if(waitingRank != null) {
            return Queue.builder()
                    .token(token)
                    .status(QueueStatus.WAITING)
                    .build();
        }

        throw new CustomException(ErrorType.TOKEN_NOT_FOUND, "토큰: " + token);
    }

    public Long getWaitingRank(String token) {
        return redisTemplate.opsForZSet().rank(WAITING_TOKEN_KEY, token);
    }
}
