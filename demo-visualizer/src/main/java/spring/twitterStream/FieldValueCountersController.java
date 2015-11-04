package spring.twitterStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikste on 04.11.15.
 */
@RestController
public class FieldValueCountersController {

    @RequestMapping("/metrics/field-value-counters")
    public String greeting() {
        return "[\"hashtags\",\"lang\"]";
    }
}
