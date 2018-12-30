package codesquad.web;

import codesquad.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    public static final String URL = "/api/questions/1/answers";
    public static final String CONTENTS = "answerTest";

    @Test
    public void create() {
        String location = createResource(URL, CONTENTS);

        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
    }
}
