package cotato.csquiz.service;

import cotato.csquiz.controller.dto.mypage.HallOfFameInfo;
import cotato.csquiz.controller.dto.mypage.HallOfFameResponse;
import cotato.csquiz.controller.dto.mypage.MyHallOfFameInfo;
import cotato.csquiz.controller.dto.mypage.MyPageMemberInfoResponse;
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

    public HallOfFameResponse findHallOfFame(Long generationId, Long memberId) {
        Generation findGeneration = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        log.info("============{}기에 존재하는 모든 득점자 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> scorerHallOfFame = createScorerHallOfFameByGeneration(findGeneration);
        log.info("============{}기에 존재하는 모든 정답자 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> answerHallOfFame = createRecordsHallOfFameInfoByGeneration(findGeneration);

        Member member = findMemberById(memberId);
        MyHallOfFameInfo myHallOfFameInfo = createMyHallOfFameInfoByGeneration(member, findGeneration);

        return HallOfFameResponse.of(scorerHallOfFame, answerHallOfFame, myHallOfFameInfo);
    }

    public MyPageMemberInfoResponse findMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);
        String originPhoneNumber = encryptService.decryptPhoneNumber(member.getPhoneNumber());

        return MyPageMemberInfoResponse.of(member, originPhoneNumber);
    }

    private MyHallOfFameInfo createMyHallOfFameInfoByGeneration(Member member, Generation generation) {
        long scorerCount = countMyScorerByGeneration(member, generation);
        long answerCount = countMyAnswerByGeneration(member, generation);

        return MyHallOfFameInfo.of(member, scorerCount, answerCount);
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 id를 가진 멤버를 찾을 수 없습니다."));
    }

    private long countMyScorerByGeneration(Member member, Generation generation) {
        List<Scorer> myScorers = findScorersByQuizzes(generation);

        return myScorers.stream()
                .filter(scorer -> scorer.getMember().equals(member))
                .count();
    }

    private long countMyAnswerByGeneration(Member member, Generation generation) {
        List<Record> myRecords = findRecordByQuizzes(generation);

        return myRecords.stream()
                .filter(record -> record.getMember().equals(member))
                .count();
    }

    private List<HallOfFameInfo> createRecordsHallOfFameInfoByGeneration(Generation generation) {
        List<Record> records = findRecordByQuizzes(generation);
        Map<Member, Long> countByMember = records.stream()
                .collect(Collectors.groupingBy(Record::getMember, Collectors.counting()));
        List<Entry<Member, Long>> sorted5MemberEntry = sortTopMemberEntry(countByMember);
        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.from(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<HallOfFameInfo> createScorerHallOfFameByGeneration(Generation generation) {
        List<Scorer> scorers = findScorersByQuizzes(generation);

        Map<Member, Long> memberScoreMap = scorers.stream()
                .collect(Collectors.groupingBy(Scorer::getMember, Collectors.counting()));
        List<Map.Entry<Member, Long>> topMemberEntry = sortTopMemberEntry(memberScoreMap);

        return topMemberEntry.stream()
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

    private List<Entry<Member, Long>> sortTopMemberEntry(Map<Member, Long> countByMember) {
        return countByMember.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(SHOW_PEOPLE_COUNT)
                .toList();
    }
}
