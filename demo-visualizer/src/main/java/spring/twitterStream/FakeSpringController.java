package spring.twitterStream;


import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.domain.FeatureCollection;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by joki02 on 04.11.2015.
 */
@RestController()
public class FakeSpringController {

    Random random = new Random(42);

    CopyOnWriteArrayList<Integer> stats = new CopyOnWriteArrayList<Integer>();

    Map<String, Integer> aggStats = new ConcurrentHashMap<String, Integer>();


    Map<String, Integer> hashStats = new ConcurrentHashMap<String, Integer>();
    Map<String, Integer> langStats = new ConcurrentHashMap<String, Integer>();

    FeatureCollection featureCollection = new FeatureCollection();


    @RequestMapping("/metrics/field-value-counters")
    public Holder fvc() {
        return new Holder(Lists.newArrayList(new Metric("languageStats"),new Metric("hashtagStats")));
    }

    @RequestMapping("/metrics/field-value-counters/{id}")
    public Counts counter(@PathVariable String id) {

        if(id.equals("languageStats")) {
            return new Counts(langStats);
        }else{
            return new Counts(hashStats);
        }
    }

    @RequestMapping("/metrics/aggregate-counters")
    public Holder aggc() {
        return new Holder(Lists.newArrayList(new Metric("intervals")));
    }

    @RequestMapping("/metrics/aggregate-counters/intervals")
    public String aggregate(){
        //TODO: Dirty hack!
        return "{\"counts\":" + stats.toString() + "}";
    }

    @RequestMapping("/geo_data")
    public FeatureCollection geojsons(){
        return featureCollection;
    }


    private class Counts {
        public Map<String, Integer> counts;

        public Counts(Map<String, Integer> counts) {
            this.counts = counts;
        }
    }


    private class Holder {
        public List<Metric> content;

        public Holder(List<Metric> content) {
            this.content = content;
        }
    }


    private class Metric {
        public String name;

        public Metric(String name) {
            this.name = name;
        }
    }

//    @PostConstruct
//    public void init() {
//        langStats.put("one", 1);
//        langStats.put("two", 1);
//        langStats.put("three", 1);
//        langStats.put("four", 1);
//        langStats.put("dfki", 1);
//        langStats.put("tu", 1);
//        langStats.put("streaming", 1);
//    }

}