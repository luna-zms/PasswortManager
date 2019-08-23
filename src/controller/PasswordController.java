package controller;

import util.PasswordQualityUtil;

public class PasswordController {

    private PMController pmController;

    /**
     *
     */
    public void generatePassword() {

    }

    /**
     *
     * Method which checks a password for its quality. The returned double is an value between 0
     * and 1. The closer the double is to 1, the higher is the quality of the password.
     *
     * @author Daniel Ginter
     * @author Alexander Korn
     * @param pwd password which has to be checked for its quality
     * @return a double between 0 - 1 which indicates the passwordquality
     */
    public double checkPasswordQuality(String pwd) {
        return PasswordQualityUtil.getNormalizedScore(pwd);
    }

}
