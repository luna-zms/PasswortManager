package model;

/**
 * This class is about setting privacy issues
 *
 * @author sopr016
 */
public class SecurityQuestion {
    private String question, answer;

    /**
     * @param question the value to be asked
     * @param answer   the value to be answered
     */
    public SecurityQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
