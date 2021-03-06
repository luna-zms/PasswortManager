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
     * constructor sets the minimal required attributes question and answer
     * @param question 
     * @param answer  
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SecurityQuestion that = (SecurityQuestion) obj;
        return question.equals(that.question) &&
                answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer);
    }

    @Override
    public String toString() {
        return "SecurityQuestion{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
