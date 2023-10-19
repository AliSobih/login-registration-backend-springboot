package com.example.registration;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/registration")
public class RegistrationController {

    private RegistrationService registrationService;
    @PostMapping("/")
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }
    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token){

        return registrationService.confirmToken(token);
    }

}
