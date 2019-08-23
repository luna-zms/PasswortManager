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
     */
    public double checkPasswordQuality(String pwd) {
        return PasswordQualityUtil.getNormalizedScore(pwd);
    }

}
