package com.globusit.fintracker.api;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void metricsEndpoint() throws Exception {
        mockMvc.perform(get("/api/analytics/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.totalExpense").exists());
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void createTransactionViaApi() throws Exception {
        Category category = categoryRepository.findByTypeOrderByNameAsc(OperationType.INCOME).get(0);
        String body = "{\"amount\":5000,\"type\":\"INCOME\",\"categoryId\":" + category.getId()
                + ",\"date\":\"2026-10-01\",\"description\":\"API\"}";
        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(5000));
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void exportExcel() throws Exception {
        mockMvc.perform(get("/api/reports/export")
                        .param("format", "xlsx")
                        .param("periodType", "MONTH")
                        .param("year", "2026")
                        .param("month", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }
}
