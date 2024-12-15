package vn.edu.uit.csbu.software_design.software_design_backend;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


public class Security {
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(;|--|\\b(SELECT|INSERT|UPDATE|DELETE|DROP|EXEC|UNION|WHERE|OR|AND|LIMIT|BENCHMARK|LOAD_FILE|INTO OUTFILE|INTO DUMPFILE)\\b)");
    private static final Pattern XSS_PATTERN = Pattern.compile(
                "<.*?>|javascript:|on\\w+=|document\\.|window\\.|alert\\(|eval\\(|\\\".*?\\\"|\\'.*?\\'", 
                Pattern.CASE_INSENSITIVE);
            
    public static boolean containsSQLInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Null or empty strings are not considered SQL injection
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public static boolean containsXSS(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Null or empty strings are not considered XSS
        }
        return XSS_PATTERN.matcher(input).find();
    }

    public byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
 
        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
     
    public String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
 
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
 
        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
 
        return hexString.toString();
    }

    public String getHashedStringOfLength(String input, int length) throws NoSuchAlgorithmException {
        if (input == null || length <= 0) {
            throw new IllegalArgumentException("Input cannot be null and length must be positive");
        }

        // Get the hashed string
        String fullHash = toHexString(getSHA(input));

        // Adjust the length: truncate or pad as necessary
        if (length <= fullHash.length()) {
            return fullHash.substring(0, length);
        } else {
            StringBuilder paddedHash = new StringBuilder(fullHash);
            while (paddedHash.length() < length) {
                paddedHash.append("0"); // Add '0' padding to the end
            }
            return paddedHash.toString();
        }
    }

    // private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
    //     KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    //     keyGen.init(128); // AES-128 (key size can be 128, 192, or 256 bits)
    //     return keyGen.generateKey();
    // }

    // // Encrypt a plain text using AES
    // public static String encrypt(String plainText) throws Exception {
    //     Cipher cipher = Cipher.getInstance("AES");
    //     cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    //     byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
    //     return Base64.getEncoder().encodeToString(encryptedBytes);
    // }

    // // Decrypt an encrypted text using AES
    // public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
    //     Cipher cipher = Cipher.getInstance("AES");
    //     cipher.init(Cipher.DECRYPT_MODE, secretKey);
    //     byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
    //     return new String(decryptedBytes);
    // }
    // askjdfblkasjdfajsdbfjasdbfdf
    // id/name|ttl|startTime
}
