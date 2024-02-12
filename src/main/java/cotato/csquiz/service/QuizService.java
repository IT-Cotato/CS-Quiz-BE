package cotato.csquiz.service;

import cotato.csquiz.domain.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.domain.dto.quiz.AllQuizzesInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.AllQuizzesResponse;
import cotato.csquiz.domain.dto.quiz.ChoiceResponse;
import cotato.csquiz.domain.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.domain.dto.quiz.CreateShortQuizRequest;
import cotato.csquiz.domain.dto.quiz.CsAdminQuizResponse;
import cotato.csquiz.domain.dto.quiz.MultipleChoiceQuizRequest;
import cotato.csquiz.domain.dto.quiz.MultipleQuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizInfoInCsQuizResponse;
import cotato.csquiz.domain.dto.quiz.QuizResponse;
import cotato.csquiz.domain.dto.quiz.ShortAnswerResponse;
import cotato.csquiz.domain.dto.quiz.ShortQuizResponse;
import cotato.csquiz.domain.dto.socket.QuizStatusResponse;
import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.MultipleQuiz;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.ShortAnswer;
import cotato.csquiz.domain.entity.ShortQuiz;
import cotato.csquiz.domain.enums.ChoiceCorrect;
import cotato.csquiz.domain.enums.QuizStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.ChoiceRepository;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ShortAnswerRepository;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private static final int RANDOM_DELAY_TIME_BOUNDARY = 10;
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
        log.info("요청된 객관식 문제의 수: {}개", multiples.size());
        multiples.forEach(request -> {
            try {
                createMultipleQuiz(findEducation, request);
            } catch (ImageException e) {
                throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
            } catch (NoSuchAlgorithmException e) {
                throw new AppException(ErrorCode.RANDOM_NUMBER_GENERATE_FAIL);
            }
        });
    }

    private void createMultipleQuiz(Education findEducation, MultipleChoiceQuizRequest request)
            throws ImageException, NoSuchAlgorithmException {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), "quiz");
        }
        MultipleQuiz createdMultipleQuiz = MultipleQuiz.builder()
                .education(findEducation)
                .number(request.getNumber())
                .question(request.getQuestion())
                .photoUrl(imageUrl)
                .appearSecond(generateRandomTime())
                .generation(findEducation.getSession().getGeneration())
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
        log.info("요청된 주관식 문제의 수: {}개", shortQuizzes.size());
        shortQuizzes.forEach(request -> {
            try {
                createShortQuiz(findEducation, request);
            } catch (ImageException e) {
                throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
            } catch (NoSuchAlgorithmException e) {
                throw new AppException(ErrorCode.RANDOM_NUMBER_GENERATE_FAIL);
            }
        });
    }

    private void createShortQuiz(Education findEducation, CreateShortQuizRequest request)
            throws ImageException, NoSuchAlgorithmException {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), "quiz");
        }
        ShortQuiz createdShortQuiz = ShortQuiz.builder()
                .education(findEducation)
                .question(request.getQuestion())
                .number(request.getNumber())
                .photoUrl(imageUrl)
                .appearSecond(generateRandomTime())
                .generation(findEducation.getSession().getGeneration())
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

    private int generateRandomTime() throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstanceStrong();
        return random.nextInt(QuizService.RANDOM_DELAY_TIME_BOUNDARY);
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

    @Transactional
    public QuizStatusResponse checkQuizStarted() {
        List<Quiz> byStatus = quizRepository.findByStatus(QuizStatus.QUIZ_ON);
        log.info("by Status {}", byStatus);
        if (byStatus.isEmpty()) {
            return QuizStatusResponse.builder()
                    .command("show")
                    .build();
        }
        Quiz quiz = byStatus.get(0);
        return QuizStatusResponse.builder()
                .command("show")
                .quizId(quiz.getId())
                .status(quiz.getStatus())
                .start(quiz.getStart())
                .build();
    }

    @Transactional
    public AllQuizzesResponse getAllQuizzes(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);
        List<MultipleQuizResponse> multiples = quizzes.stream()
                .filter(quiz -> quiz instanceof MultipleQuiz)
                .map(this::toMultipleQuizResponse)
                .toList();

        List<ShortQuizResponse> shortQuizzes = quizzes.stream()
                .filter(quiz -> quiz instanceof ShortQuiz)
                .map(this::toShortQuizResponse)
                .toList();

        return AllQuizzesResponse.builder()
                .multiples(multiples)
                .shortQuizzes(shortQuizzes)
                .build();
    }

    @Transactional
    public AllQuizzesInCsQuizResponse getAllQuizzesInCsQuiz(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);
        List<CsAdminQuizResponse> list = quizzes.stream()
                .map(this::toQuizInAdmin)
                .toList();
        return AllQuizzesInCsQuizResponse.builder()
                .quizzes(list)
                .build();
    }

    private CsAdminQuizResponse toQuizInAdmin(Quiz quiz) {
        return CsAdminQuizResponse.from(quiz);
    }

    private ShortQuizResponse toShortQuizResponse(Quiz quiz) {
        List<ShortAnswer> shortAnswers = shortAnswerRepository.findAllByShortQuiz((ShortQuiz) quiz);
        List<ShortAnswerResponse> shortAnswerResponses = shortAnswers.stream()
                .map(ShortAnswerResponse::from)
                .toList();

        ShortQuizResponse response = ShortQuizResponse.builder()
                .id(quiz.getId())
                .number(quiz.getNumber())
                .question(quiz.getQuestion())
                .image(quiz.getPhotoUrl())
                .build();
        for (ShortAnswerResponse shortAnswerResponse : shortAnswerResponses) {
            response.addShortAnswers(shortAnswerResponse);
        }

        return response;
    }

    private MultipleQuizResponse toMultipleQuizResponse(Quiz quiz) {
        List<Choice> choices = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) quiz);
        List<ChoiceResponse> choiceResponses = choices.stream()
                .map(ChoiceResponse::from)
                .toList();

        MultipleQuizResponse response = MultipleQuizResponse.builder()
                .id(quiz.getId())
                .number(quiz.getNumber())
                .question(quiz.getQuestion())
                .image(quiz.getPhotoUrl())
                .build();
        for (ChoiceResponse choiceResponse : choiceResponses) {
            response.addChoice(choiceResponse);
        }
        return response;
    }

    @Transactional
    public QuizResponse getQuiz(Long quizId) {
        Quiz findQuiz = findQuizById(quizId);
        if (findQuiz instanceof MultipleQuiz) {
            return toMultipleQuizResponse(findQuiz);
        }
        return toShortQuizResponse(findQuiz);
    }

    @Transactional
    public QuizInfoInCsQuizResponse getQuizInCsQuiz(Long quizId) {
        Quiz quiz = findQuizById(quizId);
        List<String> answerList;
        if (quiz instanceof ShortQuiz) {
            answerList = getShortQuizAnswer(quiz);
        } else {
            answerList = getMultipleQuizAnswer(quiz);
        }
        return QuizInfoInCsQuizResponse.from(quiz, answerList);
    }

    private List<String> getMultipleQuizAnswer(Quiz quiz) {
        List<Choice> allByMultipleQuiz = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) quiz);
        return allByMultipleQuiz.stream()
                .map(choice -> String.valueOf(choice.getChoiceNumber()))
                .toList();
    }

    private List<String> getShortQuizAnswer(Quiz quiz) {
        List<ShortAnswer> allByShortQuiz = shortAnswerRepository.findAllByShortQuiz((ShortQuiz) quiz);
        return allByShortQuiz.stream()
                .map(ShortAnswer::getContent)
                .toList();
    }

    @Transactional
    public void addAdditionalAnswer(AddAdditionalAnswerRequest request) {
        Quiz quiz = findQuizById(request.getQuizId());
        addAnswerInRepository(quiz, request.getAnswer());
    }

    private void addAnswerInRepository(Quiz quiz, String answer) {
        if (quiz instanceof ShortQuiz) {
            addShortAnswer((ShortQuiz) quiz, answer);
        } else if (quiz instanceof MultipleQuiz) {
            updateChoiceCorrect((MultipleQuiz) quiz, answer);
        } else {
            throw new IllegalArgumentException("Unsupported quiz type");
        }
    }

    private void addShortAnswer(ShortQuiz shortQuiz, String answer) {
        ShortAnswer shortAnswer = ShortAnswer.builder()
                .content(answer)
                .build();
        shortAnswer.matchShortQuiz(shortQuiz);
        shortAnswerRepository.save(shortAnswer);
    }

    private void updateChoiceCorrect(MultipleQuiz multipleQuiz, String answer) {
        try {
            int choiceNumber = Integer.parseInt(answer);
            Choice choice = choiceRepository.findByMultipleQuizAndChoiceNumber(multipleQuiz, choiceNumber);
            updateChoiceCorrect(choice);
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.ANSWER_VALIDATION_FAULT);
        }
    }

    private void updateChoiceCorrect(Choice choice) {
        if (choice == null) {
            throw new AppException(ErrorCode.ANSWER_VALIDATION_FAULT);
        }
        choice.changeCorrect(ChoiceCorrect.ANSWER);
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId).orElseThrow(() ->
                new AppException(ErrorCode.QUIZ_NOT_FOUND));
    }
}
