package cotato.csquiz.service;

import cotato.csquiz.domain.dto.session.AddSessionRequest;
import cotato.csquiz.domain.dto.session.AddSessionResponse;
import cotato.csquiz.domain.dto.session.CsEducationOnSessionNumberResponse;
import cotato.csquiz.domain.dto.session.SessionDescriptionRequest;
import cotato.csquiz.domain.dto.session.SessionNumRequest;
import cotato.csquiz.domain.dto.session.SessionPhotoUrlRequest;
import cotato.csquiz.domain.dto.session.UpdateSessionRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import java.util.List;
import java.util.stream.Collectors;
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
        if (request.getSessionImage() != null && !request.getSessionImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getSessionImage(), SESSION_BUCKET_DIRECTORY);
        }
        Generation findGeneration = getGeneration(request.getGenerationId());

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

    public void updateSession(UpdateSessionRequest request) throws ImageException {
        Session session = findSessionById(request.getSessionId());

        session.changeDescription(request.getDescription());
        session.updateToggle(request.getItIssue(), request.getCsEducation(), request.getNetworking());
        changePhoto(session, request.getSessionImage());
    }

    private int calculateLastSessionNumber(Generation generation) {
        List<Session> allSession = sessionRepository.findAllByGeneration(generation);
        return allSession.stream().mapToInt(Session::getNumber).max()
                .orElse(0);
    }

    @Transactional
    public void changeSessionNum(SessionNumRequest request) {
        Session session = findSessionById(request.getSessionId());
        session.changeSessionNum(session.getNumber());
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

    private void changePhoto(Session session, MultipartFile sessionImage) throws ImageException {
        String imageUrl;
        if (sessionImage != null && !sessionImage.isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(sessionImage, "session");
        } else {
            throw new ImageException(ErrorCode.IMAGE_NOT_FOUND);
        }
        session.changePhotoUrl(imageUrl);
    }

    //기수에 해당하는 세션 가지고 오기
    public List<Session> findSessionsByGenerationId(long generationId) {
        Generation generation = getGeneration(generationId);
        return sessionRepository.findAllByGeneration(generation);
    }

    public Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));
    }

    private Generation getGeneration(Long generationId) {
        return generationRepository.findById(generationId)
                .orElseThrow(() -> new AppException(ErrorCode.GENERATION_NOT_FOUND));
    }

    public List<CsEducationOnSessionNumberResponse> findAllCsOnSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new AppException(ErrorCode.GENERATION_NOT_FOUND));
        List<Session> sessions = sessionRepository.findAllByGeneration(generation);
        return sessions.stream()
                .filter(session -> session.getCsEducation() == CSEducation.CS_ON)
                .map(CsEducationOnSessionNumberResponse::from)
                .toList();
    }
}
