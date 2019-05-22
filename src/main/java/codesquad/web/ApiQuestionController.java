package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Autowired
    QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question createQuestion = qnaService.create(loginUser, question.getTitle(), question.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + createQuestion.getId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Question> show(@PathVariable Long id) {
        return new ResponseEntity<>(qnaService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update
            (@LoginUser User login, @PathVariable Long id, @Valid @RequestBody Question updateQuestion) {
        return new ResponseEntity<>
                (qnaService.update(login, id, updateQuestion.getTitle(), updateQuestion.getContents()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Question> delete(@LoginUser User login, @PathVariable Long id) throws Exception {
        return new ResponseEntity<>(qnaService.deleteQuestion(login, id), HttpStatus.OK);
    }
}
