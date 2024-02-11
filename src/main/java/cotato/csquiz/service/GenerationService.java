package cotato.csquiz.service;

import cotato.csquiz.domain.dto.generation.AddGenerationRequest;
import cotato.csquiz.domain.dto.generation.AddGenerationResponse;
import cotato.csquiz.domain.dto.generation.ChangePeriodRequest;
import cotato.csquiz.domain.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.domain.dto.generation.GenerationInfoResponse;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GenerationService {

    private final GenerationRepository generationRepository;
    private final SessionRepository sessionRepository;

    public AddGenerationResponse addGeneration(AddGenerationRequest request) {
        checkPeriodValid(request.getStartDate(), request.getEndDate());
        checkNumberValid(request.getGenerationNumber());
        Generation generation = Generation.builder()
                .number(request.getGenerationNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .sessionCount(request.getSessionCount())
                .build();
        Generation savedGeneration = generationRepository.save(generation);
        return AddGenerationResponse.builder()
                .generationId(savedGeneration.getId())
                .build();
    }

    public void changeRecruiting(ChangeRecruitingRequest request) {
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
        generation.changeRecruit(request.isStatement());
        log.info("changeRecruiting success");
    }

    public void changePeriod(ChangePeriodRequest request) {
        checkPeriodValid(request.getStartDate(), request.getEndDate());
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
        generation.changePeriod(request.getStartDate(), request.getEndDate());
        log.info("change date " + request.getStartDate() + " " + request.getEndDate());
    }

    public List<GenerationInfoResponse> getGenerations() {
        List<Generation> generations = generationRepository.findAll();
        return generations.stream()
                .map(GenerationInfoResponse::from)
                .toList();
    }
    private void checkPeriodValid(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            log.info("날짜 오류");
            throw new AppException(ErrorCode.DATE_INVALID);
        }
    }

    private void checkNumberValid(int generationNumber) {
        if (generationRepository.findByNumber(generationNumber).isPresent()) {
            throw new AppException(ErrorCode.GENERATION_NUMBER_EXIST);
        }
    }
}
