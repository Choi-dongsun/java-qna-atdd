package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class QuestionTest extends BaseTest {
    public static final Question Q1 = new Question.Builder(1L, MOVINGLINE, "질문1", "질문1의 내용").build();
    public static final Question Q2 = new Question.Builder(2L, ZINGOWORKS, "질문2", "질문2의 내용").build();
    public static final Question Q3 = new Question.Builder(3L, MOVINGLINE, "삭제된 질문3", "질문3의 내용").build();
    public static final Question Q4 = new Question.Builder(4L , ZINGOWORKS, "질문4", "질문4의 내용").build();
    public static final Question Q5 = new Question.Builder(5L , MOVINGLINE, "질문5", "질문5의 내용").build();
    public static final Question Q6 = new Question.Builder(6L , MOVINGLINE, "질문6", "질문6의 내용").build();
    public static final Question Q7 = new Question.Builder(7L , MOVINGLINE, "질문7", "질문7의 내용").build();
    public static final Question Q8 = new Question.Builder(8L , MOVINGLINE, "질문8", "질문8의 내용").build();
    public static final Question Q9 = new Question.Builder(9L , MOVINGLINE, "질문9", "질문9의 내용").build();

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

    @Test // 삭제 성공 : 답변 없음
    public void delete() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        origin.delete(MOVINGLINE);

        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class) // 삭제 실패 : 다른 사람의 답변만 존재
    public void delete_when_other_user_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answer = new Answer(1L, ZINGOWORKS, origin, "답변");
        origin.addAnswer(answer);
        origin.delete(MOVINGLINE);
    }

    @Test // 삭제 성공 : 다른사람의 삭제된 답변만 존재
    public void delete_when_other_user_deleted_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answer = new Answer(1L, ZINGOWORKS, origin, "답변");
        answer.delete();
        origin.addAnswer(answer);
        origin.delete(MOVINGLINE);

        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test // 삭제 성공 : 내 답변만 존재
    public void delete_when_only_own_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answer = new Answer(1L, MOVINGLINE, origin, "답변");
        origin.addAnswer(answer);
        origin.delete(MOVINGLINE);

        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test //  삭제 성공 : 내 답변과 타인의 삭제된 답변이 존재
    public void delete_when_own_answer_and_other_deleted_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answerOwn = new Answer(1L, MOVINGLINE, origin, "내 답변");
        Answer answerOther = new Answer(2L, ZINGOWORKS, origin, "삭제된 타인 답변");
        answerOther.delete();
        origin.addAnswer(answerOwn);
        origin.addAnswer(answerOther);
        origin.delete(MOVINGLINE);

        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class) // 삭제 실패 : 내 답변과 타인의 답변이 존재
    public void delete_when_own_answer_and_other_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answerOwn = new Answer(1L, ZINGOWORKS, origin, "내 답변");
        Answer answerOther = new Answer(2L, MOVINGLINE, origin, "삭제된 타인 답변");
        origin.addAnswer(answerOwn);
        origin.addAnswer(answerOther);
        origin.delete(MOVINGLINE);
    }
}