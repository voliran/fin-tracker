package com.globusit.fintracker.web;

import com.globusit.fintracker.service.AnalyticsService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("metrics", analyticsService.getMetrics(userService.getCurrentUser()));
        model.addAttribute("pageTitle", "Дашборд");
        return "dashboard";
    }
}
