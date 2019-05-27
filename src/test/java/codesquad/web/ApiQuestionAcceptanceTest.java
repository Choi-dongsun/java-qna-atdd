package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void create() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", Q_NEW, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void create_no_login() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions/", Q_NEW, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() {
        ResponseEntity<Question> response = template()
                .getForEntity(String.format("/api/questions/%d", Q1.getId()), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void show_when_question_not_found() {
        ResponseEntity<Question> response = template()
                .getForEntity(String.format("/api/questions/%d", NON_EXIST_ID), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update() {
//        Question newQuestion = new Question("생성질문", "생성질문의 내용");
//        String location = createResource("/api/questions", newQuestion);
//        -> 새로운 자원 생성(fixture 의존 회피)
//        Question origin = getResource(location, Question.class, defaultUser());
//        -> 해당 자원 조회
        String location = String.format("/api/questions/%d", Q5.getId());
        ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(Q_UPDATE), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getTitle()).isEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(responseEntity.getBody().getContents()).isEqualTo(Q_UPDATE.getContents());
    }

    @Test
    public void update_when_question_not_found() {
        String location = String.format("/api/questions/%d", NON_EXIST_ID);
        ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(Q_UPDATE), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update_when_other_user_access() {
        String location = String.format("/api/questions/%d", Q1.getId());
        ResponseEntity<Question> responseEntity = basicAuthTemplate(ZINGOWORKS)
                .exchange(location, HttpMethod.PUT, createHttpEntity(Q_UPDATE), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test // delete관련 test는 변경 가능성있는 fixture를 배제하고 진행해본다.
    public void delete() {
        Question newQuestion = new Question("생성질문", "생성질문의 내용");
        String location = createResource("/api/questions", newQuestion);
        ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_no_login() {
        String location = createResource("/api/questions", Q_NEW);
        ResponseEntity<Question> responseEntity = template()
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_when_other_user_access() {
        String location = createResource("/api/questions", Q_NEW);
        ResponseEntity<Question> responseEntity = basicAuthTemplate(ZINGOWORKS)
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_when_question_not_found() {
        String location = String.format("/api/questions/%d", NON_EXIST_ID);
        ResponseEntity<Question> responseEntity = basicAuthTemplate(ZINGOWORKS)
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_when_question_already_deleted() {
        String location = String.format("/api/questions/%d", Q3.getId());
        ResponseEntity<Question> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_when_not_deleted_answer_of_other_user_found() {
        String location = createResource("/api/questions", Q_NEW);
        ResponseEntity<Question> responseEntity = basicAuthTemplate(ZINGOWORKS)
                .exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}