package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).get());

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        try {
            model.addAttribute("question", qnaService.findById(id)
                    .filter(q -> q.isOwner(loginUser))
                    .orElseThrow(UnAuthorizedException::new));
            return "/qna/updateForm";
        } catch (UnAuthorizedException e) {
            return "redirect:questions/{id}";
        }
    }

    @PutMapping("{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updatedQuestion) {
        qnaService.update(loginUser, id, updatedQuestion);
        return "redirect:/questions/{id}";
    }
}
