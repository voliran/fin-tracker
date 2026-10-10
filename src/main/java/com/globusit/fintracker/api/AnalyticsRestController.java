package com.globusit.fintracker.api;

import com.globusit.fintracker.dto.DashboardMetrics;
import com.globusit.fintracker.service.AnalyticsService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsRestController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/metrics")
    public DashboardMetrics metrics() {
        return analyticsService.getMetrics(userService.getCurrentUser());
    }
}
