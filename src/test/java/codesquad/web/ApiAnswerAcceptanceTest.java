package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.AnswerTest.*;
import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.ZINGOWORKS;

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

    @Test // 변동 가능성 있는 fixture에는 의존하지 않고 진행해본다
    public void delete() {
        // 새 질문 생성
        String locationQ = createResource("/api/questions", Q_NEW);
        Long questionId = Long.parseLong(locationQ.split("/")[3]);

        // 새 답변 생성
        String locationA = createResource(String.format("/api/questions/%d/answers", questionId), A_NEW);
        softly.assertThat(locationA.startsWith(String.format("/api/questions/%d/answers/", questionId))).isTrue();

        // 답변 삭제
        ResponseEntity<Answer> response = basicAuthTemplate().exchange(locationA, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_no_login() {
        String locationQ = createResource("/api/questions", Q_NEW);
        Long questionId = Long.parseLong(locationQ.split("/")[3]);

        String locationA = createResource(String.format("/api/questions/%d/answers", questionId), A_NEW);
        softly.assertThat(locationA.startsWith(String.format("/api/questions/%d/answers/", questionId))).isTrue();

        ResponseEntity<Answer> response = template().exchange(locationA, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_when_other_user_access() {
        String locationQ = createResource("/api/questions", Q_NEW);
        Long questionId = Long.parseLong(locationQ.split("/")[3]);

        String locationA = createResource(String.format("/api/questions/%d/answers", questionId), A_NEW);
        softly.assertThat(locationA.startsWith(String.format("/api/questions/%d/answers/", questionId))).isTrue();

        ResponseEntity<Answer> response = basicAuthTemplate(ZINGOWORKS).exchange(locationA, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_when_question_not_found() {
        ResponseEntity<Answer> response = basicAuthTemplate().exchange(String.format("/api/questions/%d/answers/%d", NON_EXIST_ID, A1.getId()), HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_when_answer_not_found() {
        String locationQ = createResource("/api/questions", Q_NEW);
        Long questionId = Long.parseLong(locationQ.split("/")[3]);

        ResponseEntity<Answer> response = basicAuthTemplate(ZINGOWORKS).exchange(String.format("/api/questions/%d/answers/%d", questionId, NON_EXIST_ID), HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_when_question_already_deleted() {
//        fixture에 자유로우나 타 로직에 의존적
//        String locationQ = createResource("/api/questions", Q_NEW);
//        Long questionId = Long.parseLong(locationQ.split("/")[3]);
//
//        String locationA = createResource(String.format("/api/questions/%d/answers", questionId), A_NEW);
//        softly.assertThat(locationA.startsWith(String.format("/api/questions/%d/answers/", questionId))).isTrue();
//
//        basicAuthTemplate().exchange(String.format("/api/questions/%d", questionId),
//                        HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);
//
//        ResponseEntity<Answer> response =
//                basicAuthTemplate().exchange(locationA, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);

//        fixture 사용
        ResponseEntity<Answer> response =
                basicAuthTemplate().exchange(String.format("/api/questions/%d/answers/%d", Q11.getId(), A11.getId()),
                        HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_when_answer_already_deleted() {
        ResponseEntity<Answer> response =
                basicAuthTemplate().exchange(String.format("/api/questions/%d/answers/%d", Q4.getId(), A3.getId()),
                        HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}