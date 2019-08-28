package controller;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import util.CharGroup;
import util.PasswordGeneratorSettings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sopr018
 */
public class GeneratePasswordTest {
    private static final int FUZZER_ITERATIONS = 123456;
    private static final int LENGTH_BOUND = 1234;

    private PasswordController passwordController;
    private PasswordGeneratorSettings pgs;

    private static Stream<CharGroup> getCharGroups() {
        return Arrays.stream(CharGroup.values()).filter(group -> group != CharGroup.OTHER);
    }

    @Before
    public void setUp() {
        passwordController = new PasswordController();
        pgs = new PasswordGeneratorSettings();
        pgs.setLength(LENGTH_BOUND);
    }

    /**
     * Assert that the length of the generated password matches the length
     * passed in {@link PasswordGeneratorSettings}.
     */
    @Test
    public void testLength() {
        pgs.getCharGroups().add(CharGroup.LOWER_CASE_LETTER);
        for (int i = 0; i < LENGTH_BOUND; i++) {
            pgs.setLength(i);

            String password = passwordController.generatePassword(pgs);

            assertEquals(i, password.length());
        }
    }

    /**
     * Test that trying to generate a password without any char groups throws.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNoCharGroups() {
        passwordController.generatePassword(pgs);
    }

    /**
     * Test that generate password only contains characters from the given char groups.
     */
    @Test
    public void testInCharGroup() {
        // Filter OTHER group because it has no characters
        getCharGroups().forEach(group -> {
            pgs = new PasswordGeneratorSettings();
            pgs.selectCharGroup(group);
            String password = passwordController.generatePassword(pgs);

            checkInGroups(password, group);
        });
    }

    /**
     * Generates a bunch of passwords with random settings and checks if they meet the requirements.
     */
    @Test
    public void generatePasswordFuzzer() {
        Random rng = new Random();

        for (int i = 0; i < FUZZER_ITERATIONS; i++) {
            pgs = new PasswordGeneratorSettings();
            pgs.setLength(Math.abs(rng.nextInt(LENGTH_BOUND)));

            // Objectively best fuzzer to ever exist
            getCharGroups().filter(group -> rng.nextBoolean()).forEach(pgs::selectCharGroup);

            // Skip iteration if not char groups were selected
            if (pgs.getCharGroups().size() == 0) continue;

            String password = passwordController.generatePassword(pgs);
            assertEquals(password.length(), pgs.getLength());

            checkInGroups(password, pgs.getCharGroups().toArray(new CharGroup[]{}));
        }
    }

    /**
     * Checks if all characters of the given password are in one of the given char groups.
     *
     * @param password Password to check.
     * @param groups Characater groups to check against.
     */
    private void checkInGroups(String password, CharGroup... groups) {
        for (char c1 : password.toCharArray()) {
            boolean found = false;
            for (CharGroup group : groups) {
                for (char c2 : group.getChars()) {
                    if (c1 == c2) found = true;
                }
            }

            assertTrue(found);
        }
    }
}
