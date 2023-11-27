package cotato.csquiz.service;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.ApplicationAppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;

    public long addSession(AddSessionRequest request) {
        //운영진인지 확인하는 절차 TODO
        //사진 S3로 하는법 해야함 TODO
        Generation generation = getGeneration(request.getGenerationId());

        Session session = Session.builder()
                .number(request.getSessionNum())
                .photoUrl(request.getPhotoURL())
                .description(request.getDescription())
                .generation(generation)
                .build();
        Session savedSession = sessionRepository.save(session);
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
    public long changePhotoUrl(SessionPhotoUrlRequest request) {
        //운영진인지 확인하는 절차 TODO
        //사진 S3를 이용해 바꿔야함 TODO
        Session session = getSession(request.getSessionId());
        return session.changePhotoUrl(request.getPhotoUrl());
    }
    //기수에 해당하는 세션 가지고 오기
    public List<Session> getSessionsByGenerationId(long generationId) {
        Generation generation = getGeneration(generationId);
        return sessionRepository.findAllByGeneration(generation);
    }
    private Session getSession(long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(
                () -> new ApplicationAppException(ErrorCode.DATA_NOTFOUND, "해당 세션이 없습니다"));
    }
    private Generation getGeneration(long generationId) {
        return generationRepository.findById(generationId).orElseThrow(
                () -> new ApplicationAppException(ErrorCode.DATA_NOTFOUND, "해당 기수가 없습니다"));
    }
}
