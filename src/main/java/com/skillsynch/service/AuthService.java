
package com.skillsynch.service;

import com.skillsynch.dao.UserDAO;
import com.skillsynch.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password,
                      String selectedRole) throws Exception {

        User user = userDAO.findByUsername(username);

        if (user == null) {
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        String expectedRole = selectedRole.toLowerCase();

        if (!user.getRole().equals(expectedRole)) {
            return null;
        }

        return user;
    }
}
