package cotato.csquiz.service;

import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.domain.dto.quiz.CreateShortQuizRequest;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.ShortAnswer;
import cotato.csquiz.domain.entity.ShortQuiz;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ShortAnswerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final EducationRepository educationRepository;
    private final QuizRepository quizRepository;
    private final ShortAnswerRepository shortAnswerRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void createQuizzes(Long educationId, CreateQuizzesRequest request) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        log.info("등록할 교육 회차 : {}회차", findEducation.getNumber());
        log.info("존재하는 문제 수 : {}개", quizRepository.findAllByEducationId(educationId).size());
        quizRepository.deleteAllByEducationId(educationId);
        createShortQuizzes(findEducation, request.getShortQuizzes());
    }

    private void createShortQuizzes(Education findEducation, List<CreateShortQuizRequest> shortQuizzes) {
        shortQuizzes.forEach(request -> {
            try {
                createShortQuiz(findEducation, request);
            } catch (ImageException e) {
                throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
            }
        });
    }

    private void createShortQuiz(Education findEducation, CreateShortQuizRequest request) throws ImageException {

        String imageUrl = null;
        if (!request.getImage().isEmpty() && request.getImage() != null) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), "quiz");
        }
        ShortQuiz createdShortQuiz = ShortQuiz.builder()
                .education(findEducation)
                .question(request.getQuestion())
                .number(request.getNumber())
                .photoUrl(imageUrl)
                .build();
        log.info("주관식 문제 생성 : 사진 url {}", imageUrl);
        quizRepository.save(createdShortQuiz);
        List<ShortAnswer> shortAnswers = request.getShortAnswers().stream()
                .map(answer -> ShortAnswer.builder()
                        .content(answer.getAnswer())
                        .build())
                .toList();
        shortAnswerRepository.saveAll(shortAnswers);
        log.info("주관식 정답 생성 : {}개", shortAnswers.size());
        createdShortQuiz.addShortAnswers(shortAnswers);
    }
}
