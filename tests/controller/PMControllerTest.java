package controller;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import model.PasswordManager;

/**
 * Class to test the PMController.
 *
 * @author sopr010
 *
 */
public class PMControllerTest {

    PMController pmController;
    PasswordManager mockPasswordManager;

    String usedPassword;

    static final int NUMBER_ITERATIONS = 20;

    /**
     * Auxiliary method to create a random password with fixed length.
     *
     * @param length
     *            Length of the returned password
     * @return randomly generated password
     */
    public static byte[] createRandomPassword(int length) {
        SecureRandom pwdGenerator = new SecureRandom();

        byte[] randomBytes = new byte[20];
        pwdGenerator.nextBytes(randomBytes);

        return randomBytes;
    }

    @Before
    public void setUp() throws Exception {
        pmController = new PMController();

        byte[] randomBytes = createRandomPassword(20);
        usedPassword = new String(randomBytes);
        String safePassword = Base64.getEncoder().encodeToString(usedPassword.getBytes());

        KeySpec specs = new PBEKeySpec(safePassword.toCharArray());
        SecretKeyFactory skFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");

        try {
            mockPasswordManager = new PasswordManager(skFactory.generateSecret(specs));
        } catch ( InvalidKeySpecException  invalidKeySpec) {
            fail("Error while generating SecretKey!");
            return;
        }

        pmController.setPasswordManager(mockPasswordManager);
    }

    @Test
    public void runTestsForSetMasterPassword() {
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            System.out.print("Run Test " + (i + 1) + "/" + NUMBER_ITERATIONS + ":");
            testSetMasterPasswordRandom();
            System.out.println(" Successful!");
        }

        System.out.print("Run Test with empty password:");
        try {
            pmController.setMasterPassword("");
            fail("setMasterPassword throws no exception despite being given an empty string!");
            return;
        } catch (IllegalArgumentException emptyPasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }

        System.out.print("Run Test with no password:");
        try {
            pmController.setMasterPassword(null);
            fail("setMasterPassword throws no exception despite being given a null object!");
            return;
        } catch (IllegalArgumentException noPasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }
    }

    public void testSetMasterPasswordRandom() {
        byte[] randomBytes = createRandomPassword(20);
        String testPassword = new String(randomBytes);
        String safePassword = Base64.getEncoder().encodeToString(testPassword.getBytes());

        SecretKeyFactory skFactory;
        try {
            skFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
        } catch (NoSuchAlgorithmException noSuchAlgorithm) {
            fail("Error within SecretKeyFactory! Please check your Java installation!");
            return;
        }
        KeySpec specs = new PBEKeySpec(safePassword.toCharArray());

        SecretKey expectedKey;

        try {
            expectedKey = skFactory.generateSecret(specs);
        } catch (InvalidKeySpecException invalidKeySpec) {
            fail("Error while generating SecretKey!");
            return;
        }

        pmController.setMasterPassword(testPassword);
        assertTrue("setMasterPassword calculates a wrong key!", Arrays.equals(
                pmController.getPasswordManager().getMasterPasswordKey().getEncoded(), expectedKey.getEncoded()));
    }

    @Test
    public void runTestsForValidateMasterPassword() {
        for (int i = 0; i < NUMBER_ITERATIONS; i++) {
            System.out.print("Run Test " + (i + 1) + "/" + NUMBER_ITERATIONS + ":");
            testValidateMasterPasswordRandom();
            System.out.println(" Successful!");
        }

        System.out.print("Test with correct password:");
        assertTrue("validateMasterPassword declines the correct password!",
                pmController.validateMasterPassword(usedPassword));
        System.out.println(" Successful!");

        System.out.print("Run Test with empty password:");
        try {
            pmController.validateMasterPassword("");
            fail("validateMasterPassword throws no exception despite being given an empty string!");
            return;
        } catch (IllegalArgumentException emptyPasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }

        System.out.print("Run Test with no password:");
        try {
            pmController.validateMasterPassword(null);
            fail("validateMasterPassword throws no exception despite being given a null object!");
            return;
        } catch (IllegalArgumentException PasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }
    }

    public void testValidateMasterPasswordRandom() {
        byte[] randomBytes = createRandomPassword(20);
        String testPassword = new String(randomBytes);

        boolean result = pmController.validateMasterPassword(testPassword);
        assertTrue("validateMasterPassword accepts a wrong password or declines a correct one!",
                testPassword.equals(usedPassword) ? result : !result);
    }
}
