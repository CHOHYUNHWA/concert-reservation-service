package kr.hhplus.be.server.domain.repository;


import kr.hhplus.be.server.domain.entity.Queue;

import java.util.List;

public interface QueueRepository {
    boolean activeTokenExist(String token);

    void removeActiveToken(String token);

    Long getActiveTokenCount();

    Long getWaitingTokenCount();

    void saveActiveToken(String token);

    void saveWaitingToken(String token);

    List<String> retrieveAndRemoveWaitingToken(long neededTokens);

    Queue findToken(String token);

    Long getWaitingRank(String token);
}

