package com.globusit.fintracker.web;

import com.globusit.fintracker.domain.PeriodType;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.ReportForm;
import com.globusit.fintracker.dto.ReportPreview;
import com.globusit.fintracker.service.ReportService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping
    public String page(@ModelAttribute("form") ReportForm form, Model model) {
        User user = userService.getCurrentUser();
        if (form.getYear() == null) {
            form = reportService.currentPeriodForm();
            model.addAttribute("form", form);
        }
        ReportPreview preview = reportService.preview(form, user);
        model.addAttribute("preview", preview);
        model.addAttribute("history", reportService.history(user));
        model.addAttribute("periodTypes", PeriodType.values());
        model.addAttribute("pageTitle", "Отчёты");
        return "reports";
    }
}
