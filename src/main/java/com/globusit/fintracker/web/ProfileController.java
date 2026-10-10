package com.globusit.fintracker.web;

import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.ProfileForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String page(Model model) {
        User user = userService.getCurrentUser();
        ProfileForm form = new ProfileForm();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        model.addAttribute("form", form);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Профиль");
        return "profile";
    }

    @PostMapping
    public String update(@Valid @ModelAttribute("form") ProfileForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("pageTitle", "Профиль");
        if (bindingResult.hasErrors()) {
            return "profile";
        }
        try {
            userService.updateProfile(form);
        } catch (BusinessException ex) {
            if (ex.getMessage().contains("Пароль")) {
                bindingResult.rejectValue("newPassword", "length", ex.getMessage());
            } else {
                bindingResult.rejectValue("email", "exists", ex.getMessage());
            }
            return "profile";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Профиль обновлён");
        return "redirect:/profile";
    }
}
