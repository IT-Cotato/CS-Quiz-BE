package cotato.csquiz.service;

import cotato.csquiz.domain.dto.AllEducationResponse;
import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.AddEducationResponse;
import cotato.csquiz.domain.dto.education.EducationIdOfQuizResponse;
import cotato.csquiz.domain.dto.education.GetStatusResponse;
import cotato.csquiz.domain.dto.education.PatchEducationRequest;
import cotato.csquiz.domain.dto.education.UpdateEducationRequest;
import cotato.csquiz.domain.dto.education.WinnerInfoResponse;
import cotato.csquiz.domain.dto.quiz.KingMemberInfo;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.KingMemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.WinnerRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EducationService {

    private final SessionService sessionService;
    private final EducationRepository educationRepository;
    private final KingMemberRepository kingMemberRepository;
    private final QuizRepository quizRepository;
    private final WinnerRepository winnerRepository;

    @Transactional
    public AddEducationResponse addEducation(AddEducationRequest request) {
        Session session = sessionService.findSessionById(request.getSessionId());
        checkEducationExist(session);
        Education education = Education.builder()
                .session(session)
                .subject(request.getSubject())
                .educationNum(request.getEducationNum())
                .build();
        Education saveEducation = educationRepository.save(education);
        return AddEducationResponse.builder()
                .educationId(saveEducation.getId())
                .build();
    }

    private void checkEducationExist(Session session) {
        Optional<Education> education = educationRepository.findEducationBySession(session);
        if (education.isPresent()) {
            throw new AppException(ErrorCode.EDUCATION_EXIST);
        }
    }

    public GetStatusResponse getStatus(long educationId) {
        Education education = findEducation(educationId);
        return GetStatusResponse.builder()
                .status(education.getStatus())
                .build();
    }

    @Transactional
    public void patchEducationStatus(PatchEducationRequest request) {
        Education education = findEducation(request.getEducationId());
        education.changeStatus(request.getStatus());
    }

    private Education findEducation(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void updateSubjectAndNumber(UpdateEducationRequest request) {
        validateNotEmpty(request.newSubject());
        Education education = educationRepository.findById(request.educationId())
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        education.updateSubject(request.newSubject());
        education.updateNumber(request.newNumber());
        educationRepository.save(education);
    }

    private void validateNotEmpty(String newSubject) {
        Optional.ofNullable(newSubject)
                .filter(subject -> !subject.trim().isEmpty())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_INVALID));
    }

    public List<AllEducationResponse> getEducationListByGeneration(Long generationId) {
        List<Education> educationList = educationRepository.findBySession_Generation_Id(generationId);
        return educationList.stream()
                .map(AllEducationResponse::convertFromEducation)
                .toList();
    }

    public List<KingMemberInfo> findKingMemberInfo(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        List<KingMember> kingMembers = kingMemberRepository.findAllByEducation(findEducation);
        validateEmpty(kingMembers);
        return kingMembers.stream()
                .map(KingMember::getMember)
                .map(KingMemberInfo::from)
                .toList();
    }

    private void validateEmpty(List<KingMember> kingMembers) {
        if (kingMembers.isEmpty()) {
            throw new AppException(ErrorCode.KING_MEMBER_NOT_FOUND);
        }
    }

    public WinnerInfoResponse findWinner(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        Winner findWinner = winnerRepository.findByEducation(findEducation)
                .orElseThrow(() -> new AppException(ErrorCode.WINNER_NOT_FOUND));
        return WinnerInfoResponse.from(findWinner);
    }

    public EducationIdOfQuizResponse findEducationIdOfQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->
                new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );
        return EducationIdOfQuizResponse.from(quiz);
    }
}