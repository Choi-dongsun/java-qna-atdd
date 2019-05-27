package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;
import java.util.List;

import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class AnswerTest extends BaseTest {
    public static final Answer A1 = new Answer(1L , MOVINGLINE, Q1, "질문1의 답변1");
    public static final Answer A2 = new Answer(2L , ZINGOWORKS, Q2, "질문2의 답변2");
    public static final Answer A3 = new Answer(3L , ZINGOWORKS, Q4, "질문4의 삭제된 답변3", true);

    public static final Answer A4 = new Answer(4L , MOVINGLINE, Q6, "질문6의 답변4");
    public static final Answer A5 = new Answer(5L , ZINGOWORKS, Q9, "질문7의 답변5");
    public static final Answer A6 = new Answer(6L , ZINGOWORKS, Q9, "질문8의 삭제된 답변6", true);

    public static final Answer A7 = new Answer(7L , MOVINGLINE, Q9, "질문9의 답변7");
    public static final Answer A8 = new Answer(8L , MOVINGLINE, Q9, "질문9의 답변8");
    public static final Answer A9 = new Answer(9L , MOVINGLINE, Q9, "질문9의 삭제된 답변9", true);
    public static final Answer A10 = new Answer(10L , ZINGOWORKS, Q9, "질문9의 삭제된 답변10", true);

    public static final Answer A11 = new Answer(11L , MOVINGLINE, Q11, "삭제된 질문11의 답변11");

    public static final Answer A_NEW = new Answer(MOVINGLINE, "답변의 내용");
    public static final Answer A_UPDATE = new Answer(MOVINGLINE, "답변의 내용수정");

    public static Answer newAnswer(Long id, User user, Question question, boolean deleted) {
        return new Answer(id, user, question, "contents", deleted);
    }

    @Test
    public void delete() {
        Answer a = newAnswer(1L, MOVINGLINE, Q1, false);

        DeleteHistory deleteHistory = a.delete(MOVINGLINE);
        softly.assertThat(deleteHistory).isEqualTo(new DeleteHistory(ContentType.ANSWER, a.getId(), a.getWriter()));
        softly.assertThat(a.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_when_other_user_access() {
        A1.delete(ZINGOWORKS);
    }

    @Test(expected = IllegalStateException.class)
    public void delete_when_question_already_deleted() {
        A11.delete(MOVINGLINE);
    }

    @Test(expected = IllegalStateException.class)
    public void delete_when_answer_already_deleted() {
        A3.delete(ZINGOWORKS);
    }

    @Test
    public void deleteAll() {
        Answer a1 = newAnswer(1L, MOVINGLINE, Q1, false);
        Answer a2 = newAnswer(2L, MOVINGLINE, Q1, true);
        Answer a3 = newAnswer(3L, ZINGOWORKS, Q1, true);

        List<Answer> answers = Arrays.asList(a1, a2, a3);

        Answer.deleteAll(answers, MOVINGLINE);
        for (Answer answer : answers) softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void deleteAll_check_return_value() {
        Answer a1 = newAnswer(1L, MOVINGLINE, Q1, false);
        Answer a2 = newAnswer(2L, MOVINGLINE, Q1, true);
        Answer a3 = newAnswer(3L, ZINGOWORKS, Q1, true);

        List<Answer> answers = Arrays.asList(a1, a2, a3);

        List<DeleteHistory> deletedAnswers;
        DeleteHistory d1 = new DeleteHistory(ContentType.ANSWER, a1.getId(), a1.getWriter());
        DeleteHistory d2 = new DeleteHistory(ContentType.ANSWER, a2.getId(), a2.getWriter());
        DeleteHistory d3 = new DeleteHistory(ContentType.ANSWER, a3.getId(), a3.getWriter());
        deletedAnswers = Arrays.asList(d1, d2, d3);

        softly.assertThat(Answer.deleteAll(answers, MOVINGLINE)).isEqualTo(deletedAnswers);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAll_when_not_deleted_answer_of_other_user_found() {
        List<Answer> answers = Arrays.asList(A8, A9, A2);

        Answer.deleteAll(answers, MOVINGLINE);
    }
}