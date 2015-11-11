package spring.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikste on 11.11.15.
 */
public class FeatureCollection {
    public FeatureCollection() {
        this.features = new ArrayList<GeoJson>();
    }

    public String type = "FeatureCollection";
    public List<GeoJson> features;
}
