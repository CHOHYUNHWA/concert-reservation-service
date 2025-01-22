package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.repository.UserRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private final UserJpaRepository userJpaRepository;

    @Override
    public void existsUser(Long userId) {
        if (!userJpaRepository.existsById(userId)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
