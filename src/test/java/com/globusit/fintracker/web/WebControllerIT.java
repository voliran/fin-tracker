package com.globusit.fintracker.web;

import com.globusit.fintracker.domain.Category;
import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class WebControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
    }

    @Test
    void dashboardRequiresAuth() throws Exception {
        mockMvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void dashboardRendersForUser() throws Exception {
        mockMvc.perform(get("/dashboard")).andExpect(status().isOk()).andExpect(view().name("dashboard"));
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void transactionsPageRenders() throws Exception {
        mockMvc.perform(get("/transactions")).andExpect(status().isOk()).andExpect(view().name("transactions"));
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void addTransactionViaForm() throws Exception {
        Category category = categoryRepository.findByTypeOrderByNameAsc(OperationType.EXPENSE).get(0);
        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .param("amount", "99.90")
                        .param("type", "EXPENSE")
                        .param("categoryId", String.valueOf(category.getId()))
                        .param("date", "2026-10-01")
                        .param("description", "Тест"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void reportsPageRendersPreview() throws Exception {
        mockMvc.perform(get("/reports")).andExpect(status().isOk()).andExpect(view().name("reports"));
    }

    @Test
    @WithMockUser(username = "demo@globusit.local")
    void categoriesPageRenders() throws Exception {
        mockMvc.perform(get("/categories")).andExpect(status().isOk()).andExpect(view().name("categories"));
    }
}
