package hackinator.api;

/**
 * Created by bogumil on 04.03.16.
 */
public class HackinatorSession {
    private Integer step;
    private Integer session;
    private Integer signature;
    public String currentQuestion;
    public boolean quessVeryficationAsked;
    public boolean newGameQuestionAsked;

    public HackinatorSession() {
        this.step = 0;
        this.session = 0;
        this.signature = 0;
        this.quessVeryficationAsked = false;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Integer getSignature() {
        return signature;
    }

    public void setSignature(Integer signature) {
        this.signature = signature;
    }

    public boolean isQuessVeryficationAsked() {
        return quessVeryficationAsked;
    }

    public void setQuessVeryficationAsked(boolean quessVeryficationAsked) {
        this.quessVeryficationAsked = quessVeryficationAsked;
    }

}
