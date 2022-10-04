package june1.study.java.jsp_mybatis_oracle.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/test")
public class Hello {

    @GetMapping("/hello")
    public ModelAndView hello(@RequestParam(required = false) String name) {
        log.info("{}님, 안녕하세요.", name);
        ModelAndView mv = new ModelAndView();
        mv.addObject("name", name);
        mv.setViewName("hello");
        return mv;
    }

    @GetMapping("/hi")
    public String hi(@RequestParam(required = false) String name, Model model) {
        log.info("{}님, 안녕하세요.", name);
        model.addAttribute("name", name);
        return "hello";
    }
}
