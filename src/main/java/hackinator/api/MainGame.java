package hackinator.api;

import java.util.Scanner;

/**
 * Created by kazak on 3/2/2016.
 */
public class MainGame {
    public static String currentAnswer = "";

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);
        IJavinator ja = new Javinator();
        HackinatorSession session = null;
        System.out.println("Would you like to play a game? (yes/no): ");
        switch (Javinator.getAnswerID(scan.next())) {
            case 1:
                System.exit(0);
            case 0:
                ja.startSession();
                session = ja.getHackinatorSession();
        }

        boolean finished = false;
        while (!currentAnswer.equalsIgnoreCase("exit") && !finished) {
            if (ja.haveGuess()) {
                System.out.println("Is Your character: " + ja.getAllGuesses()[0]);
                currentAnswer = scan.next();
                ja.setHackinatorSession(session);
                ja.sendAnswer(currentAnswer);
                if (currentAnswer.equals("yes")) {
                    finished = true;
                }
            }
            if (!finished) {
                System.out.printf("Question %s : %s \n\t", ja.getStep(), ja.getCurrentQuestion());
                currentAnswer = scan.next();
                ja.setHackinatorSession(session);
                ja.sendAnswer(currentAnswer);
            }


        }

        ja.endSession();

    }

}