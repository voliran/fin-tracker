package com.globusit.fintracker.web;

import com.globusit.fintracker.dto.RegisterForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form);
        } catch (BusinessException ex) {
            bindingResult.rejectValue("email", "exists", ex.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Регистрация успешна. Войдите в систему.");
        return "redirect:/login";
    }
}
