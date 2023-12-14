package cotato.csquiz.service;

import static cotato.csquiz.domain.constant.SignUpEmailConstants.MESSAGE_PREFIX;
import static cotato.csquiz.domain.constant.SignUpEmailConstants.MESSAGE_SUBJECT;
import static cotato.csquiz.domain.constant.SignUpEmailConstants.MESSAGE_SUFFIX;
import static cotato.csquiz.domain.constant.SignUpEmailConstants.SENDER_EMAIL;
import static cotato.csquiz.domain.constant.SignUpEmailConstants.SENDER_PERSONAL;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.utils.EmailFormValidator;
import cotato.csquiz.utils.VerificationCodeRedisRepository;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;

    private final JavaMailSender mailSender;
    private final VerificationCodeRedisRepository verificationCodeRedisRepository;
    private final EmailFormValidator emailFormValidator;

    public void sendVerificationCodeToEmail(String recipient) {
        emailFormValidator.validateEmailForm(recipient);
        String verificationCode = getVerificationCode();
        log.info(verificationCode + " 인증 번호");
        verificationCodeRedisRepository.saveCodeWithEmail(recipient, verificationCode);
        sendEmailWithVerificationCode(recipient, verificationCode);
    }

    private String getVerificationCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AppException(ErrorCode.CREATE_VERIFY_CODE_FAIL);
        }
    }

    private void sendEmailWithVerificationCode(String recipient, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(RecipientType.TO, recipient);
            message.setSubject(MESSAGE_SUBJECT);
            message.setText(getVerificationText(verificationCode), "utf-8", "html");
            message.setFrom(getInternetAddress());
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVerificationText(String verificationCode) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(MESSAGE_PREFIX)
                .append(verificationCode)
                .append(MESSAGE_SUFFIX));
    }

    private InternetAddress getInternetAddress() {
        try {
            return new InternetAddress(SENDER_EMAIL, SENDER_PERSONAL);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyCode(String email, String code) {
        String savedVerificationCode = verificationCodeRedisRepository.getByEmail(email);
        validateEmailCodeMatching(savedVerificationCode, code);
        log.info("[이메일 인증 완료]: 저장된 코드 == {}", savedVerificationCode);
    }

    private void validateEmailCodeMatching(String savedVerificationCode, String code) {
        if (!savedVerificationCode.equals(code)) {
            throw new AppException(ErrorCode.CODE_NOT_MATCH);
        }
    }
}
