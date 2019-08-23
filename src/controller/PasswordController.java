package controller;

import util.PasswordQualityUtil;

public class PasswordController {
    /**
     * Generate a password from a given set of generator settings,
     * using a total of {@code pgs.getLength() * pgs.getCharGroups().size()} random numbers
     * generated by an instance of {@link java.security.SecureRandom}.
     *
     * @param pgs Generator settings containing the length and the character groups to use
     *            when generating the password. Must not be null.
     * @return A string of the given length,
     * guaranteed to consist of only the characters from the given char groups.
     * @author sopr018
     */
    public String generatePassword(PasswordGeneratorSettings pgs) {

    }

    /**
     * Method which checks a password for its quality. The returned double is an value between 0
     * and 1. The closer the double is to 1, the higher is the quality of the password.
     *
     * @param pwd password which has to be checked for its quality
     * @return a double between 0 - 1 which indicates the passwordquality
     * @author sopr012, sopr011
     */
    public double checkPasswordQuality(String pwd) {
        return PasswordQualityUtil.getNormalizedScore(pwd);
    }

}
