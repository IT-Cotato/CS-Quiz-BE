package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByQuizAndIsCorrect(Quiz quiz, boolean isCorrect);

    List<Record> findAllByQuizAndIsCorrectAndMember(Quiz quiz, boolean isCorrect, Member member);
    
    List<Record> findAllByQuizAndReply(Quiz quiz, String answer);

    List<Record> findAllByQuiz(Quiz quiz);

    Optional<Record> findByQuizAndMemberAndIsCorrect(Quiz findQuiz, Member findMember, boolean isCorrect);
}
