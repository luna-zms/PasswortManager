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
 * Class to test the PMController (non-trivial methods only).
 *
 * @author sopr010
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

    /**
     * Sets up the Test object before each test run. Therefore creates a new
     * PMController instance to be tested and a random preset password. The used
     * PasswordManager instance is saved in the attribute "mockPasswordManager".
     *
     * @throws Exception
     *             May throw a NoSuchAlgorithmException
     */
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
        } catch (InvalidKeySpecException invalidKeySpec) {
            fail("Error while generating SecretKey!");
            return;
        }

        pmController.setPasswordManager(mockPasswordManager);
    }

    /**
     * Tests whether {@link controller.PMController#setMasterPassword(String)}
     * works correctly and throws proper exceptions on invalid input. The
     * correct encryption is tested in
     * {@link controller.PMControllerTest#testSetMasterPasswordRandom()}; this
     * test is executed {@link controller.PMControllerTest#NUMBER_ITERATIONS}
     * times.
     */
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

    /**
     * Calls {@link controller.PMController#setMasterPassword(String)} with a
     * randomly generated key. Afterwards tests if the secret key set in the
     * PasswordManager was derived correctly.
     *
     * Fails if this is not the case, thus doesn't need to return anything.
     */
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

    /**
     * Tests whether
     * {@link controller.PMController#validateMasterPassword(String)} works
     * correctly and throws proper exceptions on invalid input. The correct
     * accepting/declining is tested in
     * {@link controller.PMControllerTest#testValidateMasterPasswordRandom()}
     * with random passwords; this test is executed
     * {@link controller.PMControllerTest#NUMBER_ITERATIONS} times.
     */
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
        } catch (IllegalArgumentException emptyPasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }

        System.out.print("Run Test with no password:");
        try {
            pmController.validateMasterPassword(null);
            fail("validateMasterPassword throws no exception despite being given a null object!");
        } catch (IllegalArgumentException noPasswordException) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }

        System.out.print("Run Test with no password set in mockPasswordManager: ");
        mockPasswordManager.setMasterPasswordKey(null);
        try {
            pmController.validateMasterPassword(usedPassword);
            fail("validateMasterPassword throws no exception despite the password manager contains no key!");
        } catch( IllegalStateException unmetConditionsException ) {
            // This exception should be thrown, so do nothing here
            System.out.println(" Successful!");
        }
    }

    /**
     * Calls {@link controller.PMController#validateMasterPassword} with a
     * randomly generated password. Fails if two equal passwords are declined or
     * two different ones are accepted.
     */
    public void testValidateMasterPasswordRandom() {
        byte[] randomBytes = createRandomPassword(20);
        String testPassword = new String(randomBytes);

        boolean result = pmController.validateMasterPassword(testPassword);
        assertTrue("validateMasterPassword accepts a wrong password or declines a correct one!",
                testPassword.equals(usedPassword) ? result : !result);
    }
}
