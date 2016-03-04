/**
 * Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package hackinator;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackinator.api.HackinatorSession;
import hackinator.state.StaticGamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class HackinatorSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(HackinatorSpeechlet.class);

    private static final String HACK_SESSION = "HACK_SESSION";
    private static final Integer MAX_STEPS = 15;

    private HackinatorSession getHackSession(Session session) {

        try {
            String hackinatorString = (String) session.getAttribute(HACK_SESSION);
            log.info("Session string: " + hackinatorString);
            return new ObjectMapper().readValue(hackinatorString, HackinatorSession.class);
        } catch (Exception e) {
            log.error("Cannot deserialize session ", e);
            return new HackinatorSession();
        }

    }

    private void setHackSession(Session session, HackinatorSession hackinatorSession) {
        try {
            String hackinatorString = new ObjectMapper().writeValueAsString(hackinatorSession);
            session.setAttribute(HACK_SESSION, hackinatorString);
        } catch (JsonProcessingException e) {
            log.error("cannot serialize hackintosh session");
        }
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
        StaticGamer.javinator.startSession();
        setHackSession(session, StaticGamer.javinator.getHackinatorSession());

    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse(session);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;


        if ("AnswerIntent".equals(intentName)) {
            return getNextQuestion(intent, session);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse(session);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.RepeatIntent".equals(intentName)) {
            return askResponse("Next question", getHackSession(session).currentQuestion);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse(Session session) {
        String speechText = "Welcome to the Hackinator, think of a character and I will guess it. Ready?";
        getHackSession(session).currentQuestion = speechText;
        setHackSession(session, getHackSession(session));

        return askResponse("Hackinator", speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse(Session session) {
        return getWelcomeResponse(session);
    }

    private SpeechletResponse getNextQuestion(Intent intent, Session session) {
        HackinatorSession hackinatorSession = getHackSession(session);
        StaticGamer.javinator.setHackinatorSession(hackinatorSession);


        String currentAnswer = intent.getSlot("Answer").getValue();
        String speechText = "";
        log.info("answer from user " + currentAnswer);
        String translatedAnswer = AnswerMapper.getAnswer(currentAnswer);
        log.info("translated answer " + translatedAnswer);

        boolean finished = false;
        boolean guessQuestionAsked = false;
        boolean newGame = true;
        boolean negativeAnswer = false;

        if (hackinatorSession.isQuessVeryficationAsked()) {
            guessQuestionAsked = true;
            if (translatedAnswer.equals("yes")) {
                speechText = "I'm a master of disaster. Do you want to play more?";
                hackinatorSession.newGameQuestionAsked = true;
                hackinatorSession.quessVeryficationAsked = false;
                setHackSession(session, hackinatorSession);
                StaticGamer.javinator.setHackinatorSession(hackinatorSession);
            } else {
                negativeAnswer = true;
                hackinatorSession.quessVeryficationAsked = false;
                setHackSession(session, hackinatorSession);
            }
        }

        if ((guessQuestionAsked && newGame)) {
            StaticGamer.javinator.startSession();
            hackinatorSession = StaticGamer.javinator.getHackinatorSession();
            speechText = StaticGamer.javinator.getCurrentQuestion();
            hackinatorSession.currentQuestion = speechText;
            setHackSession(session, StaticGamer.javinator.getHackinatorSession());
        }

        if (guessQuestionAsked && negativeAnswer) {
            StaticGamer.javinator.sendAnswer("no");
            speechText = StaticGamer.javinator.getCurrentQuestion();
            hackinatorSession.currentQuestion = speechText;
            setHackSession(session, StaticGamer.javinator.getHackinatorSession());
        }

        if (!guessQuestionAsked) {
            if (StaticGamer.javinator.haveGuess()) {
                if (StaticGamer.javinator.getAllGuesses().length > 0) {
                    speechText = "Is Your character " + StaticGamer.javinator.getAllGuesses()[0] + "?";
                    hackinatorSession.setQuessVeryficationAsked(true);
                    hackinatorSession.currentQuestion = speechText;
                    setHackSession(session, hackinatorSession);
                }
            } else {
                StaticGamer.javinator.sendAnswer(translatedAnswer);
                speechText = StaticGamer.javinator.getCurrentQuestion();
                hackinatorSession.currentQuestion = speechText;
                setHackSession(session, hackinatorSession);
            }
        }


        return askResponse("Next Question", speechText);
    }

    private static SpeechletResponse askResponse(String title, String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
