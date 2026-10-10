package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.ProfileForm;
import com.globusit.fintracker.dto.RegisterForm;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.exception.ResourceNotFoundException;
import com.globusit.fintracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Пользователь не авторизован");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new BusinessException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        User saved = userRepository.save(user);
        log.info("Зарегистрирован пользователь id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Transactional
    public User updateProfile(ProfileForm form) {
        User user = getCurrentUser();
        if (!user.getEmail().equalsIgnoreCase(form.getEmail()) && userRepository.existsByEmail(form.getEmail())) {
            throw new BusinessException("Email уже занят");
        }
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        if (StringUtils.hasText(form.getNewPassword())) {
            if (form.getNewPassword().length() < 6) {
                throw new BusinessException("Пароль от 6 символов");
            }
            user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        }
        User saved = userRepository.save(user);
        log.info("Обновлён профиль пользователя id={}", saved.getId());
        return saved;
    }
}
