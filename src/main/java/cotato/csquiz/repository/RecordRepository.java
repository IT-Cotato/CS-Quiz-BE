package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByQuizAndReply(Quiz quiz, String answer);

    @Query("select o from Record o join fetch o.quiz where o.isCorrect = true")
    List<Record> findAllFetchJoin(Quiz quiz);

    Optional<Record> findByQuizAndMemberAndIsCorrect(Quiz findQuiz, Member findMember, boolean isCorrect);
}
