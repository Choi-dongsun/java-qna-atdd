package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create() throws Exception {
        Question createQuestion = new Question("생성글", "생성글의 내용");
        User loginUser = new User("movingline", "123456", "name", "movingline@gmail.com");
        createQuestion.writeBy(loginUser);

        when(questionRepository.save(createQuestion)).thenReturn(createQuestion);

        softly.assertThat(qnaService.create(loginUser, createQuestion)).isEqualTo(createQuestion);
    }
}

