package model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityQuestion that = (SecurityQuestion) o;
        return question.equals(that.question) &&
                answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer);
    }
}
