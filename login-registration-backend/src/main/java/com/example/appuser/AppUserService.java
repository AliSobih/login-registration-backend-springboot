package com.example.appuser;

import com.example.email.EmailService;
import com.example.registration.token.ConfirmationTokenService;
import com.example.registration.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG,email))
                );

    }

    public String signUpUser(AppUser appUser){
        boolean userExists =
                appUserRepository.findByEmail(
                        appUser.getEmail()
                ).isPresent();
        if(userExists){
            // TODO check of attributes are same and
            // TODO if email not confirmed send confirmation email.
            if(appUser.isEnabled()){
                throw new IllegalStateException("email already exist");
            }
//            String link = "http://localhost:8080/api/registration/confirm?token=" +
//                    tokenGenerator(appUser);
//
//            emailService.send(appUser.getEmail(),link );

        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
        return tokenGenerator(appUser);
    }
    private String tokenGenerator(AppUser appUser){
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser

        );
        confirmationTokenService.saveConformationToken(confirmationToken);


        return token;
    }
    public int enableAppUser(String email) {
        return appUserRepository
                .enableAppUser(email);
    }
}
