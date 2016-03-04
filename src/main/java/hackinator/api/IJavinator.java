package hackinator.api;

/**
 * Created by kazak on 3/2/2016.
 */
public interface IJavinator {
    Integer startSession();

    String sendAnswer(String answer);

    Integer endSession();

    String[] getAllGuesses();

    Boolean haveGuess();

    Integer getStep();

    String getCurrentQuestion();

    HackinatorSession getHackinatorSession();

    void setHackinatorSession(HackinatorSession hackinatorSession);
}