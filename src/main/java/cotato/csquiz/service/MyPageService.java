package cotato.csquiz.service;

import cotato.csquiz.domain.dto.mypage.HallOfFameInfo;
import cotato.csquiz.domain.dto.mypage.HallOfFameResponse;
import cotato.csquiz.domain.dto.mypage.MyHallOfFameInfo;
import cotato.csquiz.domain.dto.mypage.MyPageMemberInfoResponse;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageService {

    private static final int SHOW_PEOPLE_COUNT = 5;
    private final GenerationRepository generationRepository;
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final EncryptService encryptService;

    public HallOfFameResponse getHallOfFame(Long generationId, String email) {
        Generation findGeneration = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
        log.info("============{}기에 존재하는 모든 퀴즈 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> scorerHallOfFame = makeScorerHallOfFame(findGeneration);
        log.info("============{}기에 존재하는 모든 득점자 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> answerHallOfFame = makeRecordsHallOfFameInfo(findGeneration);
        log.info("============{}기에 존재하는 모든 정답자 조회================", findGeneration.getNumber());
        Member member = findMemberByEmail(email);
        MyHallOfFameInfo myHallOfFameInfo1 = makeMyHallOfFameInfo(member, findGeneration);
        return HallOfFameResponse.from(scorerHallOfFame, answerHallOfFame, myHallOfFameInfo1);
    }

    public MyPageMemberInfoResponse getMemberInfo(String email) {
        Member member = findMemberByEmail(email);
        String originPhoneNumber = encryptService.decryptPhoneNumber(member.getPhoneNumber());
        return MyPageMemberInfoResponse.from(member, originPhoneNumber);
    }

    private MyHallOfFameInfo makeMyHallOfFameInfo(Member member, Generation generation) {
        long scorerCount = countMyScorer(member, generation);
        long answerCount = countMyAnswer(member, generation);
        return MyHallOfFameInfo.from(member, scorerCount, answerCount);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 멤버를 찾을 수 없습니다."));
    }

    private long countMyScorer(Member member, Generation generation) {
        List<Quiz> quizzes = quizRepository.findAllFetchJoinByScorer();
        List<Scorer> memberScorers = quizzes.stream()
                .filter(quiz -> quiz.getGeneration().equals(generation))
                .map(Quiz::getScorer)
                .filter(scorer -> scorer.getMember().equals(member))
                .toList();
        return memberScorers.size();
    }

    private long countMyAnswer(Member member, Generation generation) {
        List<Record> records = findRecordByQuizzes(member.getGeneration());
        return records.stream()
                .filter(record -> record.getMember().equals(member))
                .filter(record -> record.getQuiz().getGeneration().equals(generation))
                .toList()
                .size();
    }

    private List<HallOfFameInfo> makeRecordsHallOfFameInfo(Generation generation) {
        List<Record> records = findRecordByQuizzes(generation);
        Map<Member, Long> countByMember = records.stream()
                .collect(Collectors.groupingBy(Record::getMember, Collectors.counting()));
        List<Entry<Member, Long>> sorted5MemberEntry = sorted5MemberEntry(countByMember);
        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<HallOfFameInfo> makeScorerHallOfFame(Generation generation) {
        List<Scorer> scorers = findScorersByQuizzes(generation);
        Map<Member, Long> countByMember = scorers.stream()
                .collect(Collectors.groupingBy(Scorer::getMember, Collectors.counting()));
        List<Map.Entry<Member, Long>> sorted5MemberEntry = sorted5MemberEntry(countByMember);
        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<Scorer> findScorersByQuizzes(Generation generation) {
        List<Quiz> quizzes = quizRepository.findAllFetchJoinByScorer();
        return quizzes.stream().
                filter(quiz -> quiz.getGeneration().equals(generation))
                .map(Quiz::getScorer)
                .toList();
    }

    private List<Record> findRecordByQuizzes(Generation generation) {
        List<Quiz> quizzes = quizRepository.findAllFetchJoinRecords();
        List<Quiz> filteredQuizzes = quizzes.stream()
                .filter(quiz -> quiz.getGeneration().equals(generation))
                .toList();
        List<Record> records = new ArrayList<>();
        for (Quiz quiz : filteredQuizzes) {
            records.addAll(quiz.getRecords().stream()
                    .filter(Record::getIsCorrect)
                    .toList());
        }
        return records;
    }

    private List<Entry<Member, Long>> sorted5MemberEntry(Map<Member, Long> countByMember) {
        return countByMember.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(SHOW_PEOPLE_COUNT)
                .toList();
    }
}
