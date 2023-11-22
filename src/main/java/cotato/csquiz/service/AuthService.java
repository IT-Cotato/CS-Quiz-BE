package cotato.csquiz.service;

import cotato.csquiz.domain.dto.auth.JoinRequest;
import cotato.csquiz.domain.entity.LoginInfo;
import cotato.csquiz.repository.LoginInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final LoginInfoRepository loginInfoRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void createLoginInfo(JoinRequest request) {

        validateService.checkDuplicateEmail(request.getEmail());
        validateService.checkDuplicatePhoneNumber(request.getPhoneNumber());

        log.info("[회원 가입 서비스] : {}, {}, {}", request.getEmail(), request.getPassword(), request.getPassword());
        LoginInfo newInfo = LoginInfo.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        loginInfoRepository.save(newInfo);
    }
}
