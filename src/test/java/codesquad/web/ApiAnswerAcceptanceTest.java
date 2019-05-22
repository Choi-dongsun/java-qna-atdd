package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.AnswerTest.A_NEW;
import static codesquad.domain.QuestionTest.Q10;
import static codesquad.domain.QuestionTest.Q3;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Autowired
    AnswerRepository answerRepository;

    @Test
    public void create() {
        String location = createResource(String.format("/api/questions/%d/answers", Q10.getId()), A_NEW);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);

        softly.assertThat(location.startsWith(String.format("/api/questions/%d/answers/", Q10.getId()))).isTrue();
        softly.assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void create_no_login() {
        ResponseEntity<Void> response =
                template().postForEntity(String.format("/api/questions/%d/answers", Q10.getId()), A_NEW, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create_when_question_not_found() {
        ResponseEntity<Void> response =
                basicAuthTemplate().postForEntity(String.format("/api/questions/%d/answers", NON_EXIST_ID), A_NEW, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void create__when_question_deleted() {
        ResponseEntity<Void> response =
                basicAuthTemplate().postForEntity(String.format("/api/questions/%d/answers", Q3.getId()), A_NEW, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
