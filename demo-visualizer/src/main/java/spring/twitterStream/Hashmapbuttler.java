package spring.twitterStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by nikste on 11.11.15.
 */
@Component
public class Hashmapbuttler {
    static Logger log  = LoggerFactory.getLogger(Hashmapbuttler.class);

    CopyOnWriteArrayList<Integer> stats = new CopyOnWriteArrayList<Integer>();
    Map<String, Integer> hashStats = new ConcurrentHashMap<String, Integer>();
    Map<String, Integer> langStats = new ConcurrentHashMap<String, Integer>();

    @Autowired
    FakeSpringController fakeSpringController;

    @Scheduled(fixedDelay=1000)
    public void updateUI(){
        fakeSpringController.stats.clear();
        fakeSpringController.stats.addAll(stats);
        //stats.clear();

        fakeSpringController.hashStats.clear();
        fakeSpringController.hashStats.putAll(hashStats);
        hashStats.clear();

        fakeSpringController.langStats.clear();
        fakeSpringController.langStats.putAll(langStats);
        langStats.clear();
        //TODO: implement counter here (#elements,time)
    }
}
