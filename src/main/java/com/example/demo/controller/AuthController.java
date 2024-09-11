package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.services.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private UserService userService;
    public AuthController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("registrationSuccess", false);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,Model model) {
    	if(result.hasErrors()) {
    		model.addAttribute("registrationSuccess", false);
    		return "register";
    	}
    	
    	if(userService.isEmailAlreadyInUse(user.getEmail())) {
    		model.addAttribute("registrationSuccess", false);
    		model.addAttribute("errorMessage", "Email is already in use");
    		return "register";
    	}
    	
        userService.saveUser(user);
        model.addAttribute("registrationSuccess", true);
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
    	if ("user".equals(error)) {
            model.addAttribute("errorMessage", "No user ID found");
        } else if ("password".equals(error)) {
            model.addAttribute("errorMessage", "Wrong password");
        }
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
    	// Retrieve the current authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Email is used as the username in this case

        // Fetch the user from the database using their email
        User user = userService.findByEmail(email).orElseThrow(()->new UserNotFoundException("Username Not Found With id : "+email));

        // Add the user's name to the model to pass it to the view
        model.addAttribute("userName", user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());

        // Return the home page view
        return "home";
    }
}
