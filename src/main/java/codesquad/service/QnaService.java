package codesquad.service;

import codesquad.domain.*;
import codesquad.exception.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, String title, String contents) {
        Question question = new Question(title, contents);
        question.writeBy(loginUser);
        return questionRepository.save(question);
    }

    public Question findById(long id) throws EntityNotFoundException{
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question findByIdAndNotDeleted(long id) {
        return Optional.of(findById(id)).filter(i -> !i.isDeleted()).orElseThrow(IllegalStateException::new);
    }

    public Question findByIdWithLoginUser(User loginUser, long id) throws UnAuthorizedException {
        return Optional.of(findById(id))
                .filter(i -> i.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, String title, String contents) throws RuntimeException {
        Question original = findById(id);
        return original.update(loginUser, title, contents);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) throws Exception {
        Question original = findById(id);
        return original.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
//        answer.toQuestion(findById(questionId));
        answer.toQuestion(findByIdAndNotDeleted(questionId));
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
