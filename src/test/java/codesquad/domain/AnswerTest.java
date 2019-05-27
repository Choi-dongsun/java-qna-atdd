package codesquad.domain;

import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

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

    @Test
    public void delete() {
        A1.delete(MOVINGLINE);
        softly.assertThat(A1.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_when_other_user_access() {
        A1.delete(ZINGOWORKS);
    }
}