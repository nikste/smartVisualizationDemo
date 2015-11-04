package spring.twitterStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikste on 04.11.15.
 */

@RestController
public class FieldValueLangController {

    @RequestMapping("/metrics/field-value-counters/lang")
    public String greeting() {
         return "{\"en\":10,\"de\":12,\"dk\":5}";
    }
}
