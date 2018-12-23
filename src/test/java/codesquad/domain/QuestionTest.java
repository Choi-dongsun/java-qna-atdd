package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionTest.class);

    public static User user = new User(1, "finn", "test", "choi", "choi@naver.com");
    public static User other = new User(2, "pobi", "test", "park", "park@naver.com");

    public static Question question = new Question("title", "contents");
    public static Question updateQuestion = new Question("updatedTitle", "updatedContents");

    @Before
    public void setUp() throws Exception {
        question.writeBy(user);
        question.setId(1);
    }

    @Test
    public void update_owner() {
        question.update(user, updateQuestion);
        softly.assertThat(question.getTitle()).isEqualTo("updatedTitle");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.update(other, updateQuestion);
        softly.assertThat(question.getId()).isEqualTo(1L);
    }
}
