package codesquad.service;

import codesquad.exception.CannotDeleteException;
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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) throws EntityNotFoundException{
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws RuntimeException {
        Question original = findById(id);
        return original.update(loginUser, updatedQuestion);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) throws Exception {
        return Optional.of(findById(id))
                .filter(i -> i.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new)
                .delete();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
