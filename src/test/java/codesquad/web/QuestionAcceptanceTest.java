package codesquad.web;

import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "생성질문")
                .addParameter("contents", "생성질문의 답변").build();

        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void show() {
        ResponseEntity<String> response =
                template().getForEntity(String.format("/questions/%d", Q1.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains(Q1.getTitle())).isTrue();
        softly.assertThat(response.getBody().contains(Q1.getContents())).isTrue();
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void show_when_question_not_found() {
        ResponseEntity<String> response =
                template().getForEntity(String.format("/questions/%d", NON_EXIST_ID), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(String.format("/questions/%d/form", Q1.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains(Q1.getTitle())).isTrue();
        softly.assertThat(response.getBody().contains(Q1.getContents())).isTrue();
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response =
                template().getForEntity(String.format("/questions/%d/form", Q1.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(response.getBody().contains("사용자 아이디")).isTrue();
        softly.assertThat(response.getBody().contains("비밀번호")).isTrue();
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateForm_when_other_user_access() {
        ResponseEntity<String> response =
                basicAuthTemplate(ZINGOWORKS).getForEntity(String.format("/questions/%d/form", Q1.getId()), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void updateForm_when_question_not_found() {
        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(String.format("/questions/%d/form", NON_EXIST_ID), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    private ResponseEntity<String> update(TestRestTemplate template, long questionId) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title" , Q_UPDATE.getTitle())
                .addParameter("contents", Q_UPDATE.getContents()).put().build();

        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate(ZINGOWORKS), Q2.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template(), Q2.getId());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(response.getBody().contains("사용자 아이디")).isTrue();
        softly.assertThat(response.getBody().contains("비밀번호")).isTrue();
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update_when_other_user_access() {
        ResponseEntity<String> response = update(basicAuthTemplate(MOVINGLINE), Q2.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void update_when_question_not_found() {
        ResponseEntity<String> response = update(basicAuthTemplate(ZINGOWORKS), NON_EXIST_ID);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, long questionId) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete().build();

        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    @Test
    public void delete() { // 댓글 없는 자신의 글 삭제
        ResponseEntity<String> response = delete(basicAuthTemplate(), Q5.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template(), Q5.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_when_other_user_access() {
        ResponseEntity<String> response = delete(basicAuthTemplate(ZINGOWORKS), Q5.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath())
                .isEqualTo(String.format("/questions/%d", Q5.getId()));
    }

    @Test // 자신의 삭제되지 않은 댓글만 존재 -> 삭제 O
    public void delete_when_only_own_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(MOVINGLINE), Q6.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test // 자신의 삭제된 댓글만 존재 -> 삭제 O
    public void delete_when_only_own_deleted_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(ZINGOWORKS), Q4.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test // 타인의 삭제되지않은 댓글만 존재 -> 에러
    public void delete_when_only_other_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), Q7.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath())
                .isEqualTo(String.format("/questions/%d", Q7.getId()));
    }

    @Test // 타인의 삭제된 댓글만 존재 -> 삭제 O
    public void delete_when_only_other_deleted_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), Q8.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test // 자신의 댓글과 타인의 삭제된 댓글만 존재 - > 삭제 O
    public void delete_when_own_answer_and_other_deleted_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), Q9.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }
}
