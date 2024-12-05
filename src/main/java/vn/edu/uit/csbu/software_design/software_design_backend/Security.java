package vn.edu.uit.csbu.software_design.software_design_backend;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Security {
    
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
}
