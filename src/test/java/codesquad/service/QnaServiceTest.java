package codesquad.service;

import codesquad.domain.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static codesquad.domain.AnswerTest.*;
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

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryRepository deleteHistoryRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

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
        softly.assertThat(qnaService.findByQuestionId(Q1.getId())).isEqualTo(Q1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findById_when_question_not_found() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.empty());
        qnaService.findByQuestionId(Q1.getId());
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
    public void findByAnswerId() {
        when(answerRepository.findById(A1.getId())).thenReturn(Optional.of(A1));
        softly.assertThat(qnaService.findByAnswerId(A1.getId())).isEqualTo(A1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findByAnswerId_when_answer_not_found() {
        when(answerRepository.findById(A1.getId())).thenReturn(Optional.empty());
        qnaService.findByAnswerId(A1.getId());
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
    public void deleteQuestion() {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer a1 = newAnswer(1L, MOVINGLINE, origin, false);
        Answer a2 = newAnswer(2L, MOVINGLINE, origin, true);
        Answer a3 = newAnswer(3L, ZINGOWORKS, origin, true);
        origin.addAnswer(a1).addAnswer(a2).addAnswer(a3);

        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));
        Question question = qnaService.deleteQuestion(MOVINGLINE, origin.getId());

        softly.assertThat(question.isDeleted()).isEqualTo(true);
        List<Answer> answers = Arrays.asList(a1, a2, a3);
        for (Answer answer : answers) softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteQuestion_when_question_not_found() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(MOVINGLINE, Q1.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_when_other_user_access() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.of(Q1));

        qnaService.deleteQuestion(ZINGOWORKS, Q1.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void deleteQuestion_when_question_already_deleted() {
        when(questionRepository.findById(Q3.getId())).thenReturn(Optional.of(Q3));

        qnaService.deleteQuestion(MOVINGLINE, Q3.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_when_not_deleted_answer_of_other_user_found() {
        Question origin = newQuestion(1L, MOVINGLINE);
        Answer answer = newAnswer(1L, ZINGOWORKS, origin, false);
        origin.addAnswer(answer);

        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        softly.assertThat(qnaService.deleteQuestion(MOVINGLINE, origin.getId())).isEqualTo(origin);
    }

    @Test
    public void addAnswer() {
        when(answerRepository.save(any(Answer.class))).thenReturn(A1);
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.of(Q1));

        softly.assertThat(qnaService.addAnswer(MOVINGLINE, Q1.getId(), A1.getContents())).isEqualTo(A1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void addAnswer_when_question_not_found() {
        when(questionRepository.findById(Q1.getId())).thenReturn(Optional.empty());

        qnaService.addAnswer(MOVINGLINE, Q1.getId(), A1.getContents());
    }

    @Test(expected = IllegalStateException.class)
    public void addAnswer_when_question_already_deleted() {
        when(questionRepository.findById(Q3.getId())).thenReturn(Optional.of(Q3));

        qnaService.addAnswer(MOVINGLINE, Q3.getId(), A1.getContents());
    }

    @Test
    public void deleteAnswer() {
        Answer a = newAnswer(1L, MOVINGLINE, Q1, false);
        when(answerRepository.findById(a.getId())).thenReturn(Optional.of(a));

        Answer answer = qnaService.deleteAnswer(MOVINGLINE, a.getId());
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_when_other_user_access() {
        when(answerRepository.findById(A1.getId())).thenReturn(Optional.of(A1));

        qnaService.deleteAnswer(ZINGOWORKS, A1.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteAnswer_when_answer_not_found() {
        when(answerRepository.findById(A1.getId())).thenReturn(Optional.empty());

        qnaService.deleteAnswer(MOVINGLINE, A1.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void deleteAnswer_when_question_already_deleted() {
        when(answerRepository.findById(A11.getId())).thenReturn(Optional.of(A11));

        qnaService.deleteAnswer(MOVINGLINE, A11.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void deleteAnswer_when_answer_already_deleted() {
        when(answerRepository.findById(A9.getId())).thenReturn(Optional.of(A9));

        qnaService.deleteAnswer(MOVINGLINE, A9.getId());
    }
}