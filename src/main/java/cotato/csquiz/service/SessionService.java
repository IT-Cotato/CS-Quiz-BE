package cotato.csquiz.service;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.AddSessionResponse;
import cotato.csquiz.domain.dto.session.CsEducationOnSessionNumberResponse;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionListResponse;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.dto.session.UpdateSessionRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SessionService {

    private static final String SESSION_BUCKET_DIRECTORY = "session";
    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public AddSessionResponse addSession(AddSessionRequest request) throws ImageException {
        String imageUrl = null;
        if (isImageExist(request.getSessionImage())) {
            imageUrl = s3Uploader.uploadFiles(request.getSessionImage(), SESSION_BUCKET_DIRECTORY);
        }
        Generation findGeneration = generationRepository.findById(request.getGenerationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);
        Session session = Session.builder()
                .number(sessionNumber + 1)
                .photoUrl(imageUrl)
                .description(request.getDescription())
                .generation(findGeneration)
                .itIssue(request.getItIssue())
                .csEducation(request.getCsEducation())
                .networking(request.getNetworking())
                .build();
        Session savedSession = sessionRepository.save(session);
        log.info("세션 생성 완료");

        return AddSessionResponse.builder()
                .sessionId(savedSession.getId())
                .sessionNumber(session.getNumber())
                .build();
    }

    @Transactional
    public void updateSession(UpdateSessionRequest request) throws ImageException {
        Session session = findSessionById(request.getSessionId());

        session.changeDescription(request.getDescription());
        session.updateToggle(request.getItIssue(), request.getCsEducation(), request.getNetworking());
        Session updateSession = changePhoto(session, request.getSessionImage());
        sessionRepository.save(updateSession);
    }

    private int calculateLastSessionNumber(Generation generation) {
        List<Session> allSession = sessionRepository.findAllByGeneration(generation);
        return allSession.stream().mapToInt(Session::getNumber).max()
                .orElse(-1);
    }

    @Transactional
    public void changeSessionNumber(SessionNumRequest request) {
        Session session = findSessionById(request.getSessionId());
        session.changeSessionNumber(session.getNumber());
    }

    @Transactional
    public void changeDescription(SessionDescriptionRequest request) {
        Session session = findSessionById(request.getSessionId());
        session.changeDescription(request.getDescription());
    }

    @Transactional
    public void changePhotoUrl(SessionPhotoUrlRequest request) throws ImageException {
        Session session = findSessionById(request.getSessionId());
        changePhoto(session, request.getSessionImage());
    }

    private Session changePhoto(Session session, MultipartFile sessionImage) throws ImageException {
        if (isImageExist(sessionImage)) {
            String imageUrl = s3Uploader.uploadFiles(sessionImage, SESSION_BUCKET_DIRECTORY);
            deleteOldImage(session);
            session.changePhotoUrl(imageUrl);
        }
        if (!isImageExist(sessionImage)) {
            deleteOldImage(session);
            session.changePhotoUrl(null);
        }
        return session;
    }

    public List<SessionListResponse> findSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGeneration(generation);
        return sessions.stream()
                .map(SessionListResponse::from)
                .toList();
    }

    public Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
    }

    public List<CsEducationOnSessionNumberResponse> findAllCsOnSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
        List<Session> sessions = sessionRepository.findAllByGeneration(generation);
        return sessions.stream()
                .filter(session -> session.getCsEducation() == CSEducation.CS_ON)
                .map(CsEducationOnSessionNumberResponse::from)
                .toList();
    }

    private void deleteOldImage(Session session) throws ImageException {
        if (session.getPhotoUrl() != null) {
            s3Uploader.deleteFile(session.getPhotoUrl());
        }
    }

    private boolean isImageExist(MultipartFile sessionImage) {
        return sessionImage != null && !sessionImage.isEmpty();
    }
}
