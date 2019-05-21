package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
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
    @Autowired
    QnaService qnaService;

    @PostMapping("/")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question createQuestion = qnaService.create(loginUser, question.getTitle(), question.getContents());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + createQuestion.getId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
