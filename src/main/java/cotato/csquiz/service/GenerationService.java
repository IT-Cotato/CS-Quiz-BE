package cotato.csquiz.service;

import cotato.csquiz.domain.dto.generation.AddGenerationRequest;
import cotato.csquiz.domain.dto.generation.ChangePeriodRequest;
import cotato.csquiz.domain.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.exception.ApplicationAppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerationService {

    private final GenerationRepository generationRepository;

    //기수 추가
    public long addGeneration(AddGenerationRequest request) {
        //멤버가 운영진인지 확인 TODO
        //추가해야할 것 같은거 시작시간이 끝나는 시간보다 더 뒤면 안되는거? TODO
        LocalDate startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        LocalDate endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        Generation generation = Generation.builder()
                .name(request.getGenerationName())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Generation savedGeneration = generationRepository.save(generation);
        return savedGeneration.getId();
    }

    //모집 변경
    public long changeRecruiting(ChangeRecruitingRequest request) {
        //해당 멤버가 운영진인지 확인 TODO
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new ApplicationAppException(ErrorCode.DATA_NOTFOUND, "해당 기수가 없습니다"));
        return generation.changeRecruit(request.isStatement());
    }

    public long changePeriod(ChangePeriodRequest request) {
        //해당 멤버가 운영진인지 확인 TODO
        //추가해야할 것 같은거 시작시간이 끝나는 시간보다 더 뒤면 안되는거? TODO
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new ApplicationAppException(ErrorCode.DATA_NOTFOUND, "해당 기수가 없습니다"));
        LocalDate startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        LocalDate endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        return generation.changePeriod(startDate,endDate);
    }
}
