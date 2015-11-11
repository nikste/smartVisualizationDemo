import org.demo.connections.TwitterStreamFromFile;
import org.demo.flink.FlinkJobWindowing;
import spring.twitterStream.Application;

/**
 * Created by nikste on 11.11.15.
 */
public class StartTwitterStreamExample {

    /**
     * twitter stream from file demo with map and dashboard visualization
     * @param args
     */
    public static void main(String[] args) {
        Application.main(args);
        FlinkJobWindowing.main(args);
        TwitterStreamFromFile.main(args);
    }
}
