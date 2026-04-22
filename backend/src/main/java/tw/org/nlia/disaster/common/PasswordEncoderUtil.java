package tw.org.nlia.disaster.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class PasswordEncoderUtil {

    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private static final SecureRandom RANDOM = new SecureRandom();

    public boolean matches(String rawPassword, String encodedPassword) {
        // First try BCrypt
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        }
        // Fall back to MD5 (legacy PHP compatibility)
        return md5(rawPassword).equals(encodedPassword);
    }

    public String encode(String rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }

    public boolean isLegacyMd5(String encodedPassword) {
        return !(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$"));
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    public String generateTempPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
