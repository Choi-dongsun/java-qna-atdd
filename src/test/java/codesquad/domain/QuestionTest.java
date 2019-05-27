package codesquad.domain;

import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codesquad.domain.AnswerTest.*;
import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class QuestionTest extends BaseTest {
    public static final Question Q1 = new Question.Builder(1L, MOVINGLINE, "질문1", "질문1의 내용").build();
    public static final Question Q2 = new Question.Builder(2L, ZINGOWORKS, "질문2", "질문2의 내용").build();
    public static final Question Q3 = new Question.Builder(3L, MOVINGLINE, "삭제된 질문3", "질문3의 내용").deleted(true).build();
    public static final Question Q4 = new Question.Builder(4L , ZINGOWORKS, "질문4", "질문4의 내용").build();
    public static final Question Q5 = new Question.Builder(5L , MOVINGLINE, "질문5", "질문5의 내용").build();
    public static final Question Q6 = new Question.Builder(6L , MOVINGLINE, "질문6", "질문6의 내용").build();
    public static final Question Q7 = new Question.Builder(7L , MOVINGLINE, "질문7", "질문7의 내용").build();
    public static final Question Q8 = new Question.Builder(8L , MOVINGLINE, "질문8", "질문8의 내용").build();
    public static final Question Q9 = new Question.Builder(9L , MOVINGLINE, "질문9", "질문9의 내용").build();
    public static final Question Q10 = new Question.Builder(10L , MOVINGLINE, "질문10", "질문10의 내용").build();
    public static final Question Q11 = new Question.Builder(11L , MOVINGLINE, "삭제된 질문11", "질문11의 내용").deleted(true).build();

    public static final Question Q_NEW = new Question("질문생성", "질문의 내용");
    public static final Question Q_UPDATE = new Question("질문수정", "질문의 내용수정");

    public static Question newQuestion(Long id, User user) {
        return new Question.Builder(id, user, "title", "contents").build();
    }

    @Test
    public void update() {
        Question origin = newQuestion(1L, MOVINGLINE);
        origin.update(MOVINGLINE, Q_UPDATE.getTitle(), Q_UPDATE.getContents());

        softly.assertThat(origin.getTitle()).isEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(Q_UPDATE.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_when_other_user_access() {
        Question origin = newQuestion(1L, MOVINGLINE);
        origin.update(ZINGOWORKS, Q_UPDATE.getTitle(), Q_UPDATE.getContents());
    }

    @Test
    public void delete() {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer a1 = newAnswer(1L, MOVINGLINE, origin, false);
        Answer a2 = newAnswer(2L, MOVINGLINE, origin, true);
        Answer a3 = newAnswer(3L, ZINGOWORKS, origin, true);
        origin.addAnswer(a1).addAnswer(a2).addAnswer(a3);

        origin.delete(MOVINGLINE);

        softly.assertThat(origin.isDeleted()).isTrue();
        List<Answer> answers = Arrays.asList(a1, a2, a3);
        for (Answer answer : answers) softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test
    public void delete_check_return_value() {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer a1 = newAnswer(1L, MOVINGLINE, origin, false);
        Answer a2 = newAnswer(2L, MOVINGLINE, origin, true);
        Answer a3 = newAnswer(3L, ZINGOWORKS, origin, true);
        origin.addAnswer(a1).addAnswer(a2).addAnswer(a3);

        List<DeleteHistory> deleteHistories = new ArrayList<>();

        DeleteHistory deletedQuestion = new DeleteHistory(ContentType.QUESTION, origin.getId(), origin.getWriter());

        List<DeleteHistory> deletedAnswers;
        DeleteHistory d1 = new DeleteHistory(ContentType.ANSWER, a1.getId(), a1.getWriter());
        DeleteHistory d2 = new DeleteHistory(ContentType.ANSWER, a2.getId(), a2.getWriter());
        DeleteHistory d3 = new DeleteHistory(ContentType.ANSWER, a3.getId(), a3.getWriter());
        deletedAnswers = Arrays.asList(d1, d2, d3);

        deleteHistories.add(deletedQuestion);
        deleteHistories.addAll(deletedAnswers);

        softly.assertThat(origin.delete(MOVINGLINE)).isEqualTo(deleteHistories);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_when_other_user_access() {
        Q9.delete(ZINGOWORKS);
    }

    @Test(expected = IllegalStateException.class)
    public void delete_when_question_already_deleted() {
        Q11.delete(MOVINGLINE);
    }
}