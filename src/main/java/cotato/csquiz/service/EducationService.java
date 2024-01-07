package cotato.csquiz.service;

import cotato.csquiz.domain.dto.education.AddEducationRequest;
import cotato.csquiz.domain.dto.education.PatchStatusRequest;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.EducationStatus;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EducationService {

    private final SessionService sessionService;
    private final EducationRepository educationRepository;

    //교육 추가
    public long addEducation(AddEducationRequest request) {
        //교원팀 권한인지 확인 TODO
        Session session = sessionService.findSessionById(request.getSessionId());
        checkEducationExist(session);
        Education education = Education.builder()
                .session(session)
                .subject(request.getSubject())
                .educationNum(request.getEducationNum())
                .build();
        Education saveEducation = educationRepository.save(education);
        return saveEducation.getId();
    }

    //교육 상태(오픈여부) 바꾸기
    public void patchStatus(PatchStatusRequest request) {
        //교육팀인지 확인 TODO
        Education education = findEducation(request.getEducationId());
        education.changeStatus(request.isStatus());
    }

    //교육이 이미 존재하면 예외 발생
    private void checkEducationExist(Session session) {
        Optional<Education> education = educationRepository.findEducationBySession(session);
        if (education.isPresent()) {
            throw new AppException(ErrorCode.EDUCATION_EXIST);
        }
    }

    //교육 상태(오픈여부) 가져오기
    public EducationStatus getStatus(long educationId) {
        Education education = findEducation(educationId);
        return education.getStatus();
    }
    private Education findEducation(long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
