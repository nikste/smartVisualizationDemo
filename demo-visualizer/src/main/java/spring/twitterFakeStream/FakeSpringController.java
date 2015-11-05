package spring.twitterFakeStream;

import com.google.common.collect.Lists;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joki02 on 04.11.2015.
 */
@RestController()
public class FakeSpringController {

    Random random = new Random(42);

    @RequestMapping("/metrics/field-value-counters")
    public Holder fvc() {
        return new Holder(Lists.newArrayList(new Metric("randomstats")));
    }

    @RequestMapping("/metrics/field-value-counters/{id}")
    public Counts counter(@PathVariable String id) {

        return new Counts(tweetStats);
    }


    @RequestMapping("/metrics/aggregate-counters")
    public Holder aggc() {
        return new Holder(Lists.newArrayList(new Metric("randominterval")));
    }

    ArrayList<Integer> stats = new ArrayList<Integer>();
    Map<String, Integer> aggStats = new ConcurrentHashMap<String, Integer>();

    Map<String, Integer> tweetStats = new ConcurrentHashMap<String, Integer>();

    @Scheduled(fixedDelay = 2000)
    private void updateStats() {
        // get stats for the last X
        stats.add(random.nextInt(100));

        if (stats.size() > 10) {
            stats.remove(0);
        }

        for (int i = 0; i < stats.size(); i++) {
            aggStats.put(Integer.toString(i), stats.get(i));
        }


        for (Map.Entry<String, Integer> entry : tweetStats.entrySet()) {
            tweetStats.put(entry.getKey(), (int) Math.round(entry.getValue() * (1 + random.nextDouble())));
        }


    }

    @RequestMapping("/metrics/aggregate-counters/{id}")
    public Counts aggregate(@PathVariable String id) {

        return new Counts(aggStats);
    }

    private static class Counts {
        public Map<String, Integer> counts;

        public Counts(Map<String, Integer> counts) {
            this.counts = counts;
        }
    }


    private static class Holder {
        public List<Metric> content;

        public Holder(List<Metric> content) {
            this.content = content;
        }
    }


    private static class Metric {
        public String name;

        public Metric(String name) {
            this.name = name;
        }
    }

    @PostConstruct
    public void init() {
        tweetStats.put("one", 1);
        tweetStats.put("two", 1);
        tweetStats.put("three", 1);
        tweetStats.put("four", 1);
        tweetStats.put("dfki", 1);
        tweetStats.put("tu", 1);
        tweetStats.put("streaming", 1);
    }


}