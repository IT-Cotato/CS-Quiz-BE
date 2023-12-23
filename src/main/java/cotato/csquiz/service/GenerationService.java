package cotato.csquiz.service;

import cotato.csquiz.domain.dto.generation.AddGenerationRequest;
import cotato.csquiz.domain.dto.generation.ChangePeriodRequest;
import cotato.csquiz.domain.dto.generation.ChangeRecruitingRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GenerationService {

    private final GenerationRepository generationRepository;

    //기수 추가
    public long addGeneration(AddGenerationRequest request) {
        //멤버가 운영진인지 확인 TODO
        LocalDate startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        LocalDate endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        checkPeriodValid(startDate, endDate);
        checkNameValid(request.getGenerationName());

        Generation generation = Generation.builder()
                .name(request.getGenerationName())
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Generation savedGeneration = generationRepository.save(generation);

        return savedGeneration.getId();
    }

    //모집 변경
    public void changeRecruiting(ChangeRecruitingRequest request) {
        //해당 멤버가 운영진인지 확인 TODO
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
        generation.changeRecruit(request.isStatement());
        log.info("changeRecruiting success");
    }

    public void changePeriod(ChangePeriodRequest request) {
        //해당 멤버가 운영진인지 확인 TODO
        LocalDate startDate = LocalDate.of(request.getStartYear(), request.getStartMonth(), request.getStartDay());
        LocalDate endDate = LocalDate.of(request.getEndYear(), request.getEndMonth(), request.getEndDay());
        checkPeriodValid(startDate, endDate);
        Generation generation = generationRepository.findById(request.getGenerationId()).orElseThrow(
                () -> new AppException(ErrorCode.DATA_NOTFOUND));
        generation.changePeriod(startDate, endDate);
        log.info("change date "+startDate+ " " + endDate);
    }

    //기수 목록 알려주기
    public List<String> getGenerations(){
        return generationRepository.findAllGenerationNames();
    }

    //시작 날짜가 끝나는 날짜보다 뒤면 오류 처리
    private void checkPeriodValid(LocalDate startDate, LocalDate endDate) {
        if(endDate.isBefore(startDate)){
            log.info("날짜 오류");
            throw new AppException(ErrorCode.DATE_INVALID);
        }
    }

    private void checkNameValid(String generationName) {
        Optional<Generation> generation = generationRepository.findByName(generationName);
        if(generation.isPresent()){
            throw new AppException(ErrorCode.GENERATION_NAME_EXIST);
        }
    }



}
