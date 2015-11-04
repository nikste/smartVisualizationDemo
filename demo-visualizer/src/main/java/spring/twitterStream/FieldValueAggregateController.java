package spring.twitterStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikste on 04.11.15.
 */
@RestController
public class FieldValueAggregateController {

    @RequestMapping("/metrics/aggregate-counters")
    public String greeting() {
        return "\"links\":[\n" +
                "    {},\n" +
                "    {\n" +
                "      \"rel\":\"tweetcount\",\n" +
                "      \"href\":\"http://localhost:8080/metrics/aggrergate-counters/tweetcount\"\n" +
                "    }]";
    }

    @RequestMapping("/metrics/aggregate-counters/tweetcount")
    public String tweetcount() {
        return "{\"tweetcount\":100}";
    }
}
