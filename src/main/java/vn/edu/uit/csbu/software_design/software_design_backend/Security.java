package vn.edu.uit.csbu.software_design.software_design_backend;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


/**
 * The `Security` class in Java provides methods to detect SQL injection and XSS vulnerabilities in
 * input strings, as well as to generate SHA-3-256 hashes and manipulate the hash output length.
 */
public class Security {
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(;|--|\\b(SELECT|INSERT|UPDATE|DELETE|DROP|EXEC|UNION|WHERE|OR|AND|LIMIT|BENCHMARK|LOAD_FILE|INTO OUTFILE|INTO DUMPFILE)\\b)");
    private static final Pattern XSS_PATTERN = Pattern.compile(
                "<.*?>|javascript:|on\\w+=|document\\.|window\\.|alert\\(|eval\\(|\\\".*?\\\"|\\'.*?\\'", 
                Pattern.CASE_INSENSITIVE);

    /**
     * The function checks if a given input string contains SQL injection patterns.
     *
     * @param input The `containsSQLInjection` method checks if the input string contains any SQL injection patterns. It returns `true` if a SQL injection pattern is found in the input string, and `false` otherwise.
     * @return The method `containsSQLInjection` returns a boolean value indicating whether the input string contains a SQL injection pattern.
     */
    public static boolean containsSQLInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Null or empty strings are not considered SQL injection
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * The function checks if a given input string contains a potential Cross-Site Scripting (XSS)
     * attack pattern.
     *
     * @param input The `containsXSS` method checks if the input string contains any potential Cross-Site Scripting (XSS) patterns. It uses a regular expression pattern defined in `XSS_PATTERN` to identify XSS patterns in the input string.
     * @return The method `containsXSS` returns a boolean value indicating whether the input string contains a potential Cross-Site Scripting (XSS) attack pattern. If the input is null or empty, it returns `false` as null or empty strings are not considered XSS. Otherwise, it uses a regular expression pattern (`XSS_PATTERN`) to check if the input string contains any XSS patterns and returns the
     */
    public static boolean containsXSS(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Null or empty strings are not considered XSS
        }
        return XSS_PATTERN.matcher(input).find();
    }

    /**
     * The function `getSHA` calculates the SHA-3-256 hash of a given input string and returns it as a
     * byte array.
     *
     * @param input The `getSHA` method you provided takes a `String` input and calculates the SHA-3-256 hash of that input. The `input` parameter is the string for which you want to calculate the hash.
     * @return The method `getSHA` returns an array of bytes which represents the SHA-3-256 hash of the input string provided.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
 
        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The function `toHexString` converts a byte array hash into a hexadecimal string representation
     * with leading zeros.
     *
     * @param hash The `hash` parameter is a byte array that represents a message digest or cryptographic hash value that you want to convert to a hexadecimal string. The method `toHexString` takes this byte array and converts it into a hexadecimal representation.
     * @return The method `toHexString` returns a hexadecimal representation of the input byte array `hash`.
     */
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

    /**
     * The function `getHashedStringOfLength` takes an input string, hashes it using SHA algorithm, and
     * returns a hashed string of specified length by either truncating or padding with '0's.
     *
     * @param input  The `input` parameter is the string that you want to hash and adjust the length of.
     * @param length The `length` parameter in the `getHashedStringOfLength` method specifies the desired length of the hashed string that will be returned. The method will either truncate the hashed string to match the specified length or pad the hashed string with '0' characters at the end to reach the desired length
     * @return The method `getHashedStringOfLength` returns a hashed string of the specified length. If the hashed string is longer than the specified length, it truncates the hashed string to the specified length. If the hashed string is shorter than the specified length, it pads the hashed string with '0' characters at the end to reach the specified length.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
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
}
