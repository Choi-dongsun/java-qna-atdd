package codesquad.service;

import codesquad.UnAuthorizedException;
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

import java.util.Optional;

import static codesquad.domain.UserTest.other;
import static codesquad.domain.UserTest.user;
import static codesquad.domain.QuestionTest.question;
import static codesquad.domain.QuestionTest.updatedQuestion;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        question.writeBy(user);
        question.setId(1L);
        updatedQuestion.writeBy(user);
        updatedQuestion.setId(1L);
    }

    @Test
    public void create() {
        Question createdQuestion = new Question("title", "contents");
        createdQuestion.writeBy(user);
        when(questionRepository.save(createdQuestion)).thenReturn(createdQuestion);

        Question result = qnaService.create(user, new Question("title", "contents"));
        softly.assertThat(result).isEqualTo(createdQuestion);
    }

    @Test
    public void update() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        qnaService.update(user, 1L, new Question("updatedTitle", "updatedContents"));

        softly.assertThat(questionRepository.findById(1L).get().getTitle()).isEqualTo("updatedTitle");
        softly.assertThat(questionRepository.findById(1L).get().getContents()).isEqualTo("updatedContents");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        when(qnaService.update(user, 1L, new Question("updatedTitle", "updatedContents"))).thenReturn(updatedQuestion);
        qnaService.update(User.GUEST_USER, 1L, new Question("updatedTitle", "updatedContents"));
    }

    @Test
    public void delete() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(user, 1L);
    }

    @Test(expected = Exception.class)
    public void delete_no_login() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(null, 1L);
    }

    @Test(expected = Exception.class)
    public void delete_not_owner() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(other, 1L);
    }
}