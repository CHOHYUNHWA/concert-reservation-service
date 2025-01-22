package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.repository.UserRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private final UserJpaRepository userJpaRepository;

    @Override
    public void existsUser(Long userId) {
        if (!userJpaRepository.existsById(userId)){
            throw new CustomException(ErrorType.RESOURCE_NOT_FOUND, "검색한 USER ID: " +userId);
        }
    }
}
