package hackinator.api;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Javinator implements IJavinator{

    //private Integer session, signature, step;

    private static final Logger log = LoggerFactory.getLogger(Javinator.class);

    private Response currentResponse;
    private boolean started=false;
    private double threshold;

    private static final double DEFAULT_THRESHOLD=80.0;

    private String currentAnswer="";
    private String currentQuestion="";
    private Integer step;
    private Integer session;
    private Integer signature;

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getSession() {
        return session;
    }

    public int getSignature() {
        return signature;
    }

    private ObjectMapper mapper = new ObjectMapper();
    private final String USER_AGENT = "Mozilla/5.0";
    private final String CORE_URL = "http://api-en1.akinator.com/ws/";

    public Javinator(double threshold){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.threshold = threshold;
    }

    public Javinator(){
        this(DEFAULT_THRESHOLD);
    }


    private double getProgression() {
        if(this.getCurrentResponse().getParameters().getStep_information()==null)
            return this.getCurrentResponse().getParameters().getProgression();
        else
            return this.getCurrentResponse().getParameters().getStep_information().getProgression();
    }

    public Boolean haveGuess(){
        String[] g = null;
        if(getProgression()>threshold){
            g = getAllGuesses();
        }
        return g!=null && g.length>0 ;
    }

    public String[] getAllGuesses(){
        String url = CORE_URL+"list?session=" + this.getSession() +
                "&signature=" + this.getSignature() +
                "&step=" + (this.step) +
                "&mode_question=0";
        Response response = sendRequest(url);
        if(response.getParameters().getElements()!=null){
            String[] out = new String[response.getParameters().getElements().length];
            int i=0;
            for(Elements element: response.getParameters().getElements()){
                out[i++]=element.getElement().getName();
            }
            return out;
        }
        return null;
    }


    public Integer getStep() {
        return this.step+1;
    }

    private Response getCurrentResponse() {
        return currentResponse;
    }

    public Integer startSession() {

        String url = CORE_URL+"new_session?partner=1&player=Hackinator";
        Response response = sendRequest(url);
        this.currentResponse = response;
        this.session = response.getParameters().getIdentification().getSession();
        this.signature = response.getParameters().getIdentification().getSignature();
        this.started=true;
        this.step=0;
        this.currentQuestion=getQuestion();
        return 0;

    }

    private String getQuestion() {
        if(this.started) {
            return this.currentResponse.getParameters().getQuestion() != null ?
                    this.currentResponse.getParameters().getQuestion() :
                    this.currentResponse.getParameters().getStep_information().getQuestion();
        }
        return null;
    }

    public String sendAnswer(String answer) {

        String url = CORE_URL+"answer?session=" + this.getSession() +
                "&signature=" + this.getSignature() +
                "&step=" + (this.step++) +
                "&answer=" + getAnswerID(answer);
        this.currentResponse=sendRequest(url);
        setCurrentQuestion(getQuestion());
        return getCurrentQuestion();

    }

    public Integer endSession() {
        this.session=null;
        this.step=null;
        return null;
    }

    private Response sendRequest(String url){
        try {
            Response response = mapper.readValue(new URL(url), Response.class);
//            System.out.println("\t" + url + "\n" + response);
            log.debug("Response: "+response);
            return response;
        } catch (IOException e) {
            System.out.println("ERROR!!!" + e.getLocalizedMessage());
        }
        return null;
    }

    public static int getAnswerID(String ans){
        int id = 0;
        switch (ans.toLowerCase()){
            case "yes":{}
            case "y":{
                id = 0;
                break;
            }
            case "no":{}
            case "n":{
                id = 1;
                break;
            }
            case "dontknow":{}
            case "d":{
                id = 2;
                break;
            }
            case "probably":{}
            case "p":{
                id = 3;
                break;
            }
            case "probablynot":{}
            case "pn":{
                id = 4;
                break;
            }
        }
        return id;
    }

}