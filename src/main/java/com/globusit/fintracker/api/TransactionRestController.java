package com.globusit.fintracker.api;

import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.TransactionForm;
import com.globusit.fintracker.service.TransactionService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping
    public List<Map<String, Object>> list() {
        User user = userService.getCurrentUser();
        return transactionService.findAllForUser(user).stream()
                .map(this::toJson)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody TransactionForm form) {
        Transaction saved = transactionService.save(form, userService.getCurrentUser());
        return toJson(saved);
    }

    private Map<String, Object> toJson(Transaction tx) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", tx.getId());
        json.put("amount", tx.getAmount());
        json.put("type", tx.getType());
        json.put("category", tx.getCategory().getName());
        json.put("date", tx.getDate());
        json.put("description", tx.getDescription());
        return json;
    }
}
