package codesquad.service;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.MOVINGLINE;
import static codesquad.domain.UserTest.ZINGOWORKS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(QnaServiceTest.class);

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create() throws Exception {
        when(questionRepository.save(any(Question.class))).thenReturn(Q1);
        softly.assertThat(qnaService.create(MOVINGLINE, Q1.getTitle(), Q1.getContents())).isEqualTo(Q1);
    }

    @Test
    public void findById() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.of(Q1));
        softly.assertThat(qnaService.findById(Q1.getId())).isEqualTo(Q1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findById_when_question_not_found() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.empty());
        qnaService.findById(Q1.getId());
    }

    @Test
    public void findByIdWithLoginUser() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.of(Q1));
        softly.assertThat(qnaService.findByIdWithLoginUser(MOVINGLINE, Q1.getId())).isEqualTo(Q1);
    }

    @Test(expected = UnAuthorizedException.class)
    public void findByIdWithLoginUser_when_other_user_access() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.of(Q1));
        qnaService.findByIdWithLoginUser(ZINGOWORKS, Q1.getId());
    }

    @Test
    public void update() {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));
        Question updated = qnaService.update(MOVINGLINE, origin.getId(), Q_UPDATE.getTitle(), Q_UPDATE.getContents());

        softly.assertThat(updated.getTitle()).isEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(updated.getContents()).isEqualTo(Q_UPDATE.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_when_other_user_access() {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        qnaService.update(ZINGOWORKS, origin.getId(), Q_UPDATE.getTitle(), Q_UPDATE.getContents());
    }

    @Test
    public void delete() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        softly.assertThat(qnaService.deleteQuestion(MOVINGLINE, origin.getId())).isEqualTo(origin);
    }

    @Test(expected = EntityNotFoundException.class)
    public void delete_when_question_not_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.empty());

        softly.assertThat(qnaService.deleteQuestion(MOVINGLINE, origin.getId())).isEqualTo(origin);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_when_other_user_access() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        softly.assertThat(qnaService.deleteQuestion(ZINGOWORKS, origin.getId())).isEqualTo(origin);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_when_other_user_answer_found() throws Exception {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answer = new Answer(1L, ZINGOWORKS, origin, "답변");
        origin.addAnswer(answer);

        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        softly.assertThat(qnaService.deleteQuestion(MOVINGLINE, origin.getId())).isEqualTo(origin);
    }
}