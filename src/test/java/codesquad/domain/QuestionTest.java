package codesquad.domain;

import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class QuestionTest extends BaseTest {
    public static final Question Q1 = new Question(1L, MOVINGLINE, "질문1", "질문1의 내용");
    public static final Question Q2 = new Question(2L, ZINGOWORKS, "질문2", "질문2의 내용");
    public static final Question Q3 = new Question(3L, MOVINGLINE, "삭제된 질문3", "질문3의 내용");
    public static final Question Q4 = new Question(4L , ZINGOWORKS, "질문4", "질문4의 내용");

    public static final Question Q_UPDATE = new Question("질문수정", "질문의 내용수정");

    public static Question newQuestion(Long id, User user) {
        return new Question(id, user, "title", "contents");
    }

    @Test
    public void update() {
        Question origin = newQuestion(1L, MOVINGLINE);
        origin.update(MOVINGLINE, Q_UPDATE);

        softly.assertThat(origin.getTitle()).isEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(Q_UPDATE.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_when_other_user_access() {
        Question origin = newQuestion(1L, MOVINGLINE);
        origin.update(ZINGOWORKS, Q_UPDATE);

        softly.assertThat(origin.getTitle()).isNotEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(origin.getContents()).isNotEqualTo(Q_UPDATE.getContents());
    }
}