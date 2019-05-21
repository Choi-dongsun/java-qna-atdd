package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void create() {
        Question newQuestion = new Question("생성질문", "생성질문의 답변");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_no_login() {
        Question newQuestion = new Question("생성질문", "생성질문의 답변");
        ResponseEntity<Void> response = template().postForEntity("/api/questions/", newQuestion, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
