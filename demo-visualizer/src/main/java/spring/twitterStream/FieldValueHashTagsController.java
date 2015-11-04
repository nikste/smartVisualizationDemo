package spring.twitterStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikste on 04.11.15.
 */
@RestController
public class FieldValueHashTagsController {

    @RequestMapping("/metrics/field-value-counters/hashtags")
    public String greeting() {
        return "{\"#penis\":10,\"#thailand\":12,\"aluhut\":1}";
    }
}
