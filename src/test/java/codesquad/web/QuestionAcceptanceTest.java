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
        softly.assertThat(response.getBody().contains("사용자 아이디")).isTrue();
        softly.assertThat(response.getBody().contains("비밀번호")).isTrue();
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "생성질문")
                .addParameter("contents", "생성질문의 답변").build();

        return template.postForEntity("/questions", request, String.class);
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
        softly.assertThat(response.getBody().contains("사용자 아이디")).isTrue();
        softly.assertThat(response.getBody().contains("비밀번호")).isTrue();
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
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
    }

    @Test
    public void updateForm_when_other_user_access() {
        ResponseEntity<String> response =
                basicAuthTemplate(ZINGOWORKS).getForEntity(String.format("/questions/%d/form", Q1.getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_when_question_not_found() {
        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(String.format("/questions/%d/form", NON_EXIST_ID), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
    }

    @Test
    public void update_when_other_user_access() {
        ResponseEntity<String> response = update(basicAuthTemplate(MOVINGLINE), Q2.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_when_question_not_found() {
        ResponseEntity<String> response = update(basicAuthTemplate(ZINGOWORKS), NON_EXIST_ID);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, long questionId) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete().build();

        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    @Test // 삭제 성공
    public void delete() {
        ResponseEntity<String> response = delete(basicAuthTemplate(), Q5.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(questionRepository.findById(Q5.getId()).get().isDeleted()).isTrue();
    }

    @Test // 삭제 실패 : 타인이 내 질문을 삭제 시도
    public void delete_when_other_user_access() {
        ResponseEntity<String> response = delete(basicAuthTemplate(ZINGOWORKS), Q6.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findById(Q6.getId()).get().isDeleted()).isFalse();
    }

    @Test // 삭제 실패 : 타인 댓글이 달린 내 질문을 삭제 시도
    public void delete_when_other_user_answer_found() {
        ResponseEntity<String> response = delete(basicAuthTemplate(MOVINGLINE), Q7.getId());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findById(Q7.getId()).get().isDeleted()).isFalse();
    }
}
