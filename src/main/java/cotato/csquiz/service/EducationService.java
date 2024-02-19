package cotato.csquiz.service;

import cotato.csquiz.domain.dto.AllEducationResponse;
import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.AddEducationResponse;
import cotato.csquiz.domain.dto.education.GetStatusResponse;
import cotato.csquiz.domain.dto.education.PatchEducationRequest;
import cotato.csquiz.domain.dto.education.PatchSubjectRequest;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.EducationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EducationService {

    private final SessionService sessionService;
    private final EducationRepository educationRepository;

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

    public void patchEducationStatus(PatchEducationRequest request) {
        Education education = findEducation(request.getEducationId());
        education.changeStatus(request.getStatus());
    }

    private Education findEducation(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public void patchSubject(PatchSubjectRequest request) {
        validateNotEmpty(request.getNewSubject());

        Education education = educationRepository.findById(request.getEducationId())
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        education.updateSubject(request.getNewSubject());
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
}