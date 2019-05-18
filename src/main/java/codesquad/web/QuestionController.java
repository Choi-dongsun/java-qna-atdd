package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.exception.UnAuthorizedException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name="qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, String title, String contents) {
        Question question = new Question(title, contents);
        qnaService.create(user, question);

        return "redirect:/";
    }

    @GetMapping("")
    public String list() {
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute(qnaService.findById(id));

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        try {
            model.addAttribute("question", Optional.of(qnaService.findById(id))
                    .filter(q -> q.isOwner(loginUser))
                    .orElseThrow(UnAuthorizedException::new));
            return "/qna/updateForm";
        } catch (UnAuthorizedException e) {
            return String.format("redirect:/questions/%d", id);
        }
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, Question updateQuestion) {
        try {
            qnaService.update(loginUser, id, updateQuestion);
        } catch (UnAuthorizedException e) {
        } finally {
            return String.format("redirect:/questions/%d", id);
        }
    }
}