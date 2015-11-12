package spring.domain;

/**
 * Created by nikste on 12.11.15.
 */
public class BoundingBox {
    // already initialized
    double neLat;
    double neLng;
    double swLat;
    double swLng;

    public double getNeLat() {
        return neLat;
    }

    public double getNeLng() {
        return neLng;
    }

    public double getSwLat() {
        return swLat;
    }

    public double getSwLng() {
        return swLng;
    }

    public void setNeLat(double neLat) {
        this.neLat = neLat;
    }

    public void setNeLng(double neLng) {
        this.neLng = neLng;
    }

    public void setSwLat(double swLat) {
        this.swLat = swLat;
    }

    public void setSwLng(double swLng) {
        this.swLng = swLng;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "neLat=" + neLat +
                ", neLng=" + neLng +
                ", swLat=" + swLat +
                ", swLng=" + swLng +
                '}';
    }
}
