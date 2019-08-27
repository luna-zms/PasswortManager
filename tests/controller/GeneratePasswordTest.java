package controller;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import util.CharGroup;
import util.PasswordGeneratorSettings;

import static org.junit.Assert.*;

/**
 * @author sopr018
 */
public class GeneratePasswordTest {
    private PasswordController passwordController;
    private PasswordGeneratorSettings pgs;


    @Before
    public void setUp() {
        passwordController = new PasswordController();
        pgs = new PasswordGeneratorSettings();
        pgs.setLength(1234);
    }

    /**
     * Assert that the length of the generated password matches the length
     * passed in {@link PasswordGeneratorSettings}.
     */
    @Test
    public void testLength() {
        pgs.getCharGroups().add(CharGroup.LOWER_CASE_LETTER);
        for (int i = 0; i < 1234; i++) {
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
        Arrays.stream(CharGroup.values())
              .filter(group -> group != CharGroup.OTHER)
              .forEach(group -> {
                  pgs.getCharGroups().add(group);
                  String password = passwordController.generatePassword(pgs);

                  for (char c1 : password.toCharArray()) {
                      boolean found = false;
                      for (char c2 : group.getChars()) {
                          if (c1 == c2) found = true;
                      }

                      assertTrue(found);
                  }

                  pgs.getCharGroups().remove(group);
              });
    }
}
