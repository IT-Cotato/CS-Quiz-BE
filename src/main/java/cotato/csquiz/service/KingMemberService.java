package cotato.csquiz.service;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.repository.KingMemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ScorerRepository;
import cotato.csquiz.repository.WinnerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KingMemberService {

    private final QuizRepository quizRepository;
    private final KingMemberRepository kingMemberRepository;
    private final ScorerRepository scorerRepository;
    private final WinnerRepository winnerRepository;

    @Transactional
    public void calculateKingMember(Quiz quiz) {
        Education education = quiz.getEducation();
        List<Quiz> quizzes = quizRepository.findAllByEducationId(education.getId());
        List<Scorer> scorersInEducation = findScorerFrom(quizzes);
        List<Member> members = findKingMembersFrom(scorersInEducation);
        List<KingMember> kingMembers = members.stream()
                .map(member -> KingMember.of(member, education))
                .toList();
        kingMemberRepository.saveAll(kingMembers);
        if (kingMembers.size() == 1) {
            Winner winner = Winner.of(kingMembers.get(0).getMember(), education);
            winnerRepository.save(winner);
        }
    }

    private List<Scorer> findScorerFrom(List<Quiz> quizzes) {
        return quizzes.stream()
                .flatMap(tempQuiz -> scorerRepository.findAllByQuiz(tempQuiz).stream())
                .toList();
    }

    private List<Member> findKingMembersFrom(List<Scorer> scorers) {
        Map<Member, Long> countByMember = scorers.stream()
                .collect(Collectors.groupingBy(Scorer::getMember, Collectors.counting()));
        Optional<Long> maxCount = countByMember.values().stream().max(Long::compareTo);
        return countByMember.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount.orElse(null)))
                .map(Entry::getKey)
                .toList();
    }

    @Transactional
    public void saveWinner(Quiz quiz) {
        Education education = quiz.getEducation();
        if (winnerRepository.findByEducation(education).isEmpty()) {
            Scorer findScorer = scorerRepository.findByQuiz(quiz)
                    .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈엔 득점자가 존재하지 않습니다."));
            Winner winner = Winner.of(findScorer.getMember(), education);
            winnerRepository.save(winner);
        }
    }
}
