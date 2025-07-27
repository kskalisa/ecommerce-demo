package com.ecommerce.assignment.controller.auth;

import com.ecommerce.assignment.dto.UserRegistDTO;
import com.ecommerce.assignment.model.User;
import com.ecommerce.assignment.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;  // Add this import

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;



    @GetMapping("/signin")
    public String login(){
        return "main/login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("user", new UserRegistDTO());
        return "main/signup";
    }



    @PostMapping("/register")
    public String register( @Valid @ModelAttribute("user") UserRegistDTO userDto,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes
    ){
       try{
           if (userService.isEmailAlreadyRegistered(userDto.getEmail())) {
               result.rejectValue("email", "error.email",
                       "There is already an account registered with that email");
               return "main/signup";
           }

           if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
               result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
               return "main/signup";
           }

           if (!isPasswordStrong(userDto.getPassword())) {
               result.rejectValue("password", "error.password", "Password must be at least 8 characters long");
               return "main/signup";
           }

           if (result.hasErrors()) {
               return "main/signup";
           }

           userService.save(userDto);
           redirectAttributes.addFlashAttribute("message", "You have been registered successfully");
           return "redirect:/auth/signin";
       }
       catch (Exception e){
           log.error("Error during registration", e);
           result.reject("error.global", "Something went wrong during registration: "+e.getMessage());
           return "main/signup";

       }


    }

    private boolean isPasswordStrong( String password) {
        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+!])[A-Za-z0-9@#$%^&+!]{8,}$";
        return password.matches(pattern);
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/dashboard";
            }
        }

        return "redirect:/checkout";
    }

    @GetMapping("/login-fail")
    public String loginFail(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("message", "Invalid username or password");
        return "redirect:/auth/signin";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/auth/signin";
    }







}
