package com.rushi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public static List<RegistrationForm> REGISTRATIONS = new ArrayList<>();

    public static class RegistrationForm implements Serializable {
        @NotEmpty(message = "Name is required")
        private String name;

        @NotEmpty(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotEmpty(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
        private String phone;

        @NotEmpty(message = "Course selection is required")
        private String course;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getCourse() { return course; }
        public void setCourse(String course) { this.course = course; }
    }

    @GetMapping("/")
    public String showForm(Model model, HttpServletRequest request) {

        logger.info("Rendering registration form for client IP: {}", request.getRemoteAddr());
        model.addAttribute("form", new RegistrationForm());
        model.addAttribute("courses", getCourses());
        model.addAttribute("clientIp", request.getRemoteAddr());
        model.addAttribute("serverIp", request.getLocalAddr());
        return "registration";
    }

    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("form") RegistrationForm form,
                              BindingResult result,
                              Model model,
                              HttpServletRequest request) {
        logger.info("Processing registration for email: {}", form.getEmail());
        model.addAttribute("clientIp", request.getRemoteAddr());
        model.addAttribute("serverIp", request.getLocalAddr());
        model.addAttribute("courses", getCourses());

        if (result.hasErrors()) {
            logger.warn("Validation errors found for registration form. Error count: {}", result.getErrorCount());
            logger.debug("Validation error details: {}", result.getAllErrors());
            return "registration";
        }

        // Mock email sending
        logger.info("Sending email to: {}", form.getEmail());
        logger.info("Hello {}, you selected course: {}", form.getName(), form.getCourse());

        model.addAttribute("name", form.getName());
        model.addAttribute("emailSent", true);
        model.addAttribute("course", form.getCourse());
        REGISTRATIONS.add(form);
        logger.info("Registration successful for: {}", form.getName());
        return "success";
    }

    private List<String> getCourses() {
        return Arrays.asList("DevOps", "AWS", "Azure", "Terraform", "Kubernetes","Python","Linux");
    }
}
