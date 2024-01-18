package cotato.csquiz.service;

import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.domain.dto.quiz.CreateShortQuizRequest;
import cotato.csquiz.domain.dto.quiz.MultipleChoiceQuizRequest;
import cotato.csquiz.domain.dto.quiz.QuizStatusResponse;
import cotato.csquiz.domain.entity.*;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.ChoiceRepository;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ShortAnswerRepository;
import java.util.ArrayList;
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
    private final ChoiceRepository choiceRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void createQuizzes(Long educationId, CreateQuizzesRequest request) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new AppException(ErrorCode.EDUCATION_NOT_FOUND));
        log.info("등록할 교육 회차 : {}회차", findEducation.getNumber());
        log.info("존재하는 문제 수 : {}개", quizRepository.findAllByEducationId(educationId).size());
        validateDuplicateNumber(request);
        quizRepository.deleteAllByEducationId(educationId);
        createShortQuizzes(findEducation, request.getShortQuizzes());
        createMultipleQuizzes(findEducation, request.getMultiples());
    }

    private void createMultipleQuizzes(Education findEducation, List<MultipleChoiceQuizRequest> multiples) {
        multiples.forEach(request -> {
            try {
                createMultipleQuiz(findEducation, request);
            } catch (ImageException e) {
                throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
            }
        });
    }

    private void createMultipleQuiz(Education findEducation, MultipleChoiceQuizRequest request) throws ImageException {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), "quiz");
        }
        MultipleQuiz createdMultipleQuiz = MultipleQuiz.builder()
                .education(findEducation)
                .number(request.getNumber())
                .question(request.getQuestion())
                .photoUrl(imageUrl)
                .build();
        log.info("객관식 문제 생성, 사진 url {}", imageUrl);
        quizRepository.save(createdMultipleQuiz);
        List<Choice> choices = request.getChoices().stream()
                .map(choice -> Choice.builder()
                        .choiceNumber(choice.getNumber())
                        .content(choice.getContent())
                        .isCorrect(choice.getIsAnswer())
                        .build())
                .toList();
        choiceRepository.saveAll(choices);
        log.info("객관식 선지 생성 : {}개", choices.size());
        createdMultipleQuiz.addChoices(choices);
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

    private void validateDuplicateNumber(CreateQuizzesRequest request) {
        List<Integer> numbers = new ArrayList<>();
        List<Integer> multiples = request.getMultiples().stream()
                .map(MultipleChoiceQuizRequest::getNumber)
                .toList();
        List<Integer> shortQuizzes = request.getShortQuizzes().stream()
                .map(CreateShortQuizRequest::getNumber)
                .toList();
        numbers.addAll(multiples);
        numbers.addAll(shortQuizzes);
        long count = numbers.stream()
                .distinct()
                .count();
        if (count != numbers.size()) {
            throw new AppException(ErrorCode.QUIZ_NUMBER_DUPLICATED);
        }
    }

    public QuizStatusResponse checkQuizStarted() {
        List<Quiz> byStatus = quizRepository.findByStatus(QuizStatus.ON);
        log.info("by Status {}",byStatus);
        if (byStatus.isEmpty()) {
            return QuizStatusResponse.builder()
                    .command("show")
                    .build();
        }
        Quiz quiz = byStatus.get(0);
        return QuizStatusResponse.builder()
                .command("show")
                .quizNum(quiz.getId())
                .status(quiz.getStatus())
                .start(quiz.getStart())
                .build();
    }
}
