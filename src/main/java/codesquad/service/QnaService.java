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

    public Question findByQuestionId(long id) throws EntityNotFoundException{
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Answer findByAnswerId(long id) throws EntityNotFoundException{
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question findByIdWithLoginUser(User loginUser, long id) throws UnAuthorizedException {
        return Optional.of(findByQuestionId(id))
                .filter(i -> i.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, String title, String contents) throws RuntimeException {
        Question original = findByQuestionId(id);
        return original.update(loginUser, title, contents);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) {
        Question original = findByQuestionId(id);
        deleteHistoryService.saveAll(original.delete(loginUser));
        return original;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(findByQuestionId(questionId));
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer original = findByAnswerId(id);
        deleteHistoryService.save(original.delete(loginUser));
        return original;
    }
}
