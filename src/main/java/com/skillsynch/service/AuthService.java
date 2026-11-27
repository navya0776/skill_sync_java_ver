
package com.skillsynch.service;

import com.skillsynch.dao.UserDAO;
import com.skillsynch.model.User;
import com.skillsynch.model.UserRole;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password,
                      UserRole selectedRole) throws Exception {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || selectedRole == null) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());

        if (user == null) {
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        if (user.getRole() != selectedRole) {
            return null;
        }

        return user;
    }
}
