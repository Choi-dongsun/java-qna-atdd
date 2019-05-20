package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    QuestionRepository questionRepository;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questions = questionRepository.findByDeleted(false);
        model.addAttribute("questions", questions);
        return "/home";
    }
}
