package com.globusit.fintracker.web;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.dto.CategoryForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CategoryForm());
        }
        fill(model);
        return "categories";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            fill(model);
            return "categories";
        }
        try {
            categoryService.save(form);
        } catch (BusinessException ex) {
            bindingResult.rejectValue("name", "exists", ex.getMessage());
            fill(model);
            return "categories";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Категория сохранена");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        CategoryForm form = new CategoryForm();
        form.setId(id);
        form.setName(categoryService.getById(id).getName());
        form.setType(categoryService.getById(id).getType());
        model.addAttribute("form", form);
        fill(model);
        return "categories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (bindingResult.hasErrors()) {
            fill(model);
            return "categories";
        }
        categoryService.save(form);
        redirectAttributes.addFlashAttribute("successMessage", "Категория обновлена");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория удалена");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Нельзя удалить категорию, которая используется в транзакциях");
        }
        return "redirect:/categories";
    }

    private void fill(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("types", OperationType.values());
        model.addAttribute("pageTitle", "Категории");
    }
}
