package codesquad.web;

import codesquad.domain.Answer;
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
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Autowired
    QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@PathVariable Long questionId, @LoginUser User loginUser, @Valid @RequestBody Answer answer) {
        Answer createAnswer = qnaService.addAnswer(loginUser, questionId, answer.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(String.format("/api/questions/%d/answers/%d", questionId, createAnswer.getId())));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Answer> delete(@LoginUser User loginUser, @PathVariable Long questionId, @PathVariable Long id) {
        return new ResponseEntity<>(qnaService.deleteAnswer(loginUser, questionId, id), HttpStatus.OK);
    }
}
