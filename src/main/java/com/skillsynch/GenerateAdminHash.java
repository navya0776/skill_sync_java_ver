package com.skillsynch;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateAdminHash {

    public static void main(String[] args) {

        String password = "YourAdminPassword";

        String hash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        System.out.println("Password Hash: " + hash);
    }
}