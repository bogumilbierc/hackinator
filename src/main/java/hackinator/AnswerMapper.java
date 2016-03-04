package hackinator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bogumil on 04.03.16.
 */
public class AnswerMapper {

    private static final Map<String, List<String>> answersMap = new LinkedHashMap<String, List<String>>() {{
        put("yes", Arrays.asList("yes", "yep", "yeah", "sure"));
        put("no", Arrays.asList("no", "nope", "not really", "nah"));
        put("dontknow", Arrays.asList("i don't know", "i do not know", "not sure"));

        put("probably", Arrays.asList("sort of", "probably yes", "probably yep", "probably yeap", "probably yeah", "probably", "maybe", "think so"));
        put("probablynot", Arrays.asList("probably no", "probably nope", "don't think so", "probably not"));

    }};


    public static String getAnswer(String userAnswer) {
        for (String key : answersMap.keySet()) {
            if (answersMap.get(key).contains(userAnswer)) {
                return key;
            }
        }
        return "failed";
    }
}
