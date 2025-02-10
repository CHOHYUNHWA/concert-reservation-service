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
        Double expireAt = redisTemplate.opsForZSet().score(ACTIVE_TOKEN_KEY, token);
        return expireAt != null && expireAt > System.currentTimeMillis(); // 현재 시간과 비교
    }

    @Override
    public void removeActiveToken(String token) {
        redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, token);
    }

    @Override
    public Long getActiveTokenCount() {
        return redisTemplate.opsForZSet().zCard(ACTIVE_TOKEN_KEY);
    }

    @Override
    public void saveActiveToken(String token) {
        long expireAt = System.currentTimeMillis() + TOKEN_TTL.toMillis(); // 현재 시간 + TTL(10분)
        redisTemplate.opsForZSet().add(ACTIVE_TOKEN_KEY, token, expireAt);
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
        Double expireAt = redisTemplate.opsForZSet().score(ACTIVE_TOKEN_KEY, token);

        if (expireAt != null) {
            if (expireAt > System.currentTimeMillis()) {
                return Queue.builder()
                        .token(token)
                        .status(QueueStatus.ACTIVE)
                        .build();
            } else {
                redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, token);
            }
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

    @Override
    public void removeExpiredTokens() {
        long now = System.currentTimeMillis();

        Set<String> expiredTokens = redisTemplate.opsForZSet().rangeByScore(ACTIVE_TOKEN_KEY, 0, now);

        if (expiredTokens != null && !expiredTokens.isEmpty()) {
            redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, expiredTokens.toArray());
        }
    }


    //테스트용
    @Override
    public void removeOldestTwoActiveTokens() {
        // 가장 오래된 2개의 토큰 조회 (ZRANGE: 정순 조회)
        Set<String> oldestTokens = redisTemplate.opsForZSet().range(ACTIVE_TOKEN_KEY, 0, 1);

        if (oldestTokens != null && !oldestTokens.isEmpty()) {
            // 가장 오래된 2개의 토큰 삭제
            redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, oldestTokens.toArray());
        }
    }

    //test용
    @Override
    public void expiredActiveToken(String token) {
        // 현재 시간보다 이전으로 `score`(만료 시간) 설정하여 즉시 만료 처리
        long expiredTime = System.currentTimeMillis() - 1000; // 1초 전으로 설정 (즉시 만료)

        // 만료된 시간으로 업데이트
        redisTemplate.opsForZSet().add(ACTIVE_TOKEN_KEY, token, expiredTime);
    }
}
