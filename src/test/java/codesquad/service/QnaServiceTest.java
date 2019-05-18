package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
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
        when(questionRepository.save(Q1)).thenReturn(Q1);
        softly.assertThat(qnaService.create(MOVINGLINE, Q1)).isEqualTo(Q1);
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
    public void update() {
        Question origin = newQuestion(1L, MOVINGLINE);
        when(questionRepository.findById(origin.getId())).thenReturn(Optional.of(origin));

        softly.assertThat(qnaService.update(MOVINGLINE, origin.getId(), Q_UPDATE).getTitle()).isEqualTo(Q_UPDATE.getTitle());
        softly.assertThat(qnaService.update(MOVINGLINE, origin.getId(), Q_UPDATE).getContents()).isEqualTo(Q_UPDATE.getContents());
    }
}