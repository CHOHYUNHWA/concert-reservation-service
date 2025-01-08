package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.repository.UserRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void existsUser(Long userId){
        userRepository.existsUser(userId);
    }

}
