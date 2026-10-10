package com.globusit.fintracker.web;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.TransactionForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.service.CategoryService;
import com.globusit.fintracker.service.TransactionService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        User user = userService.getCurrentUser();
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TransactionForm());
        }
        fillCommon(model, user);
        return "transactions";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") TransactionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            fillCommon(model, user);
            return "transactions";
        }
        try {
            transactionService.save(form, user);
        } catch (BusinessException ex) {
            bindingResult.rejectValue("categoryId", "mismatch", ex.getMessage());
            fillCommon(model, user);
            return "transactions";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Транзакция сохранена");
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("form", transactionService.toForm(transactionService.getById(id, user)));
        fillCommon(model, user);
        return "transactions";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") TransactionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        form.setId(id);
        if (bindingResult.hasErrors()) {
            fillCommon(model, user);
            return "transactions";
        }
        transactionService.save(form, user);
        redirectAttributes.addFlashAttribute("successMessage", "Транзакция обновлена");
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transactionService.delete(id, userService.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Транзакция удалена");
        return "redirect:/transactions";
    }

    private void fillCommon(Model model, User user) {
        model.addAttribute("transactions", transactionService.findAllForUser(user));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("types", OperationType.values());
        model.addAttribute("pageTitle", "Транзакции");
    }
}
