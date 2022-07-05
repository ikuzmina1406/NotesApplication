package ua.goit.notesApplication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.goit.notesApplication.user.UserDto;
import ua.goit.notesApplication.user.UserRole;
import ua.goit.notesApplication.user.UserService;

@Component
public class AppReadyListener {
    @Value("${DEFAULT_ADMIN_NAME}")
    private String defaultAdminName;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppReadyListener(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void appReady() {
        try {
            if (userService.findUserByUsername(defaultAdminName) != null) {
                return;
            }
        } catch (UsernameNotFoundException e) {
            addAdminUser();
        }
    }

    private void addAdminUser() {
        UserDto adminUser = new UserDto();
        adminUser.setUsername(defaultAdminName);
        adminUser.setPassword(passwordEncoder.encode(defaultAdminPassword));
        adminUser.setUserRole(UserRole.ROLE_ADMIN);

        userService.save(adminUser);
    }
}
