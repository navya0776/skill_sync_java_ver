

package com.skillsynch.service;

import com.skillsynch.dao.UserDAO;
import com.skillsynch.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Locale;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(
            String username,
            String password,
            String selectedRole
    ) throws Exception {

        // Validate input fields
        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || selectedRole == null || selectedRole.isBlank()) {

            return null;
        }

        // Clean input
        username = username.trim();
        selectedRole = selectedRole.trim().toLowerCase(Locale.ROOT);

        // Find user by username
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return null;
        }

        // Validate stored password hash
        if (user.getPasswordHash() == null
                || user.getPasswordHash().isBlank()) {

            return null;
        }

        // Verify password
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        // Validate stored role
        if (user.getRole() == null
                || !user.getRole().equalsIgnoreCase(selectedRole)) {

            return null;
        }

        // Login successful
        return user;
    }
}
