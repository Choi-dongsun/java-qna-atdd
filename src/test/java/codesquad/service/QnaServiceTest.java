package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
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

    public static User testUser = new User(1, "finn", "1234", "동선", "choi@naver.com");
    public static Question testQuestion = new Question("title", "contents");

    @Before
    public void setUp() throws Exception {
        testQuestion.writeBy(testUser);
        testQuestion.setId(1);
    }

    @Test
    public void create() {
        Question createdQuestion = new Question("title", "contents");
        createdQuestion.writeBy(testUser);
        when(questionRepository.save(createdQuestion)).thenReturn(createdQuestion);

        Question result = qnaService.create(testUser, new Question("title", "contents"));
        softly.assertThat(result).isEqualTo(createdQuestion);
    }

}
