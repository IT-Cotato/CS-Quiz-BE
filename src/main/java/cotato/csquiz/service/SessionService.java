package cotato.csquiz.service;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;
    private final S3Uploader s3Uploader;

    public long addSession(AddSessionRequest request) throws ImageException {
        //운영진인지 확인하는 절차 TODO
        String imageUrl = null;
        if(!(request.getSessionImage().isEmpty())) {
            imageUrl = s3Uploader.uploadFiles(request.getSessionImage(), "session");
        }
        Generation generation = getGeneration(request.getGenerationId());

        Session session = Session.builder()
                .number(request.getSessionNum())
                .photoUrl(imageUrl)
                .description(request.getDescription())
                .generation(generation)
                .build();
        Session savedSession = sessionRepository.save(session);
        log.info("세션 생성 완료");
        return savedSession.getId();
    }
    //차수 바꾸기
    public int changeSessionNum(SessionNumRequest request) {
        //운영진인지 확인하는 절차 TODO
        Session session = getSession(request.getSessionId());
        return session.changeSessionNum(request.getSessionNum());
    }
    //한줄소개 바꾸기
    public long changeDescription(SessionDescriptionRequest request) {
        //운영진인지 확인하는 절차 TODO
        Session session = getSession(request.getSessionId());
        return session.changeDescription(request.getDescription());
    }
    //사진 바꾸기
    public long changePhotoUrl(SessionPhotoUrlRequest request) throws ImageException {
        //운영진인지 확인하는 절차 TODO
        Session session = getSession(request.getSessionId());
        String imageUrl = null;
        if(!(request.getSessionImage().isEmpty())) {
            imageUrl = s3Uploader.uploadFiles(request.getSessionImage(), "session");
        }
        return session.changePhotoUrl(imageUrl);
    }
    //기수에 해당하는 세션 가지고 오기
    public List<Session> getSessionsByGenerationId(long generationId) {
        Generation generation = getGeneration(generationId);
        return sessionRepository.findAllByGeneration(generation);
    }
    private Session getSession(long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
    }
    private Generation getGeneration(long generationId) {
        return generationRepository.findById(generationId).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
    }
}