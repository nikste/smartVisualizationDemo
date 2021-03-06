package org.demo.flink;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by nikste on 12.11.15.
 */
public class FlinkJobCoMapRandomSampleTest {



    @Test
    public void testJsonFilter() {

        boolean insideGermany = false;


        double neLat = 55.05814;
        double neLng = 15.04205;

        double swLat = 47.27021;
        double swLng =  5.86624;

        String s = "{\"filter_level\":\"low\",\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"en\",\"in_reply_to_status_id_str\":null,\"id\":646691060148781057,\"in_reply_to_user_id_str\":null,\"timestamp_ms\":\"1443018138335\",\"in_reply_to_status_id\":null,\"created_at\":\"Wed Sep 23 14:22:18 +0000 2015\",\"favorite_count\":0,\"place\":{\"id\":\"37439688c6302728\",\"bounding_box\":{\"type\":\"Polygon\",\"coordinates\":[[[11.360589,48.061634],[11.360589,48.248124],[11.722918,48.248124],[11.722918,48.061634]]]},\"place_type\":\"city\",\"name\":\"Munich\",\"attributes\":{},\"country_code\":\"DE\",\"url\":\"https://api.twitter.com/1.1/geo/id/37439688c6302728.json\",\"country\":\"Deutschland\",\"full_name\":\"Munich, Bavaria\"},\"coordinates\":{\"type\":\"Point\",\"coordinates\":[11.57412,48.14814]},\"text\":\"#cytwombly @ Museum Brandhorst https://t.co/YvsJhkeru0\",\"contributors\":null,\"geo\":{\"type\":\"Point\",\"coordinates\":[48.14814,11.57412]},\"entities\":{\"trends\":[],\"symbols\":[],\"urls\":[{\"expanded_url\":\"https://instagram.com/p/7-e6AblU5X/\",\"indices\":[31,54],\"display_url\":\"instagram.com/p/7-e6AblU5X/\",\"url\":\"https://t.co/YvsJhkeru0\"}],\"hashtags\":[{\"text\":\"cytwombly\",\"indices\":[0,10]}],\"user_mentions\":[]},\"source\":\"<a href=\\\"http://instagram.com\\\" rel=\\\"nofollow\\\">Instagram<\\/a>\",\"favorited\":false,\"in_reply_to_user_id\":null,\"retweet_count\":0,\"id_str\":\"646691060148781057\",\"user\":{\"location\":\"\",\"default_profile\":false,\"profile_background_tile\":true,\"statuses_count\":113166,\"lang\":\"en\",\"profile_link_color\":\"999698\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/440252035/1438364469\",\"id\":440252035,\"following\":null,\"protected\":false,\"favourites_count\":94175,\"profile_text_color\":\"333333\",\"verified\":false,\"description\":\"she broke up with me i have small eyes\",\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"000000\",\"name\":\"gland\",\"profile_background_color\":\"FFFFFF\",\"created_at\":\"Sun Dec 18 19:15:24 +0000 2011\",\"default_profile_image\":false,\"followers_count\":333,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/627172155025981440/K7jb8qtD_normal.jpg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/498641873292886016/3mFDKX3n.jpeg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/498641873292886016/3mFDKX3n.jpeg\",\"follow_request_sent\":null,\"url\":\"http://csection.tumblr.com\",\"utc_offset\":10800,\"time_zone\":\"Bucharest\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":161,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"wronghat\",\"id_str\":\"440252035\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/627172155025981440/K7jb8qtD_normal.jpg\",\"listed_count\":7,\"is_translator\":false}}\n";


        if(!(neLat == 0.0 && neLng == 0.0 &&
                swLat == 0.0 && swLng == 0.0)){
            Configuration conf2 = Configuration.defaultConfiguration();
            Configuration conf = conf2.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

            LinkedHashMap geo = JsonPath.using(conf).parse(s).read("$['geo']");

            if(geo != null){
                if(geo.containsKey("coordinates")) {
                    insideGermany = true;
                    ArrayList<Number> coordinates = (ArrayList<Number>) geo.get("coordinates");//.read("[0]",Double.class);
                    double lat = Double.valueOf(coordinates.get(0).toString());
                    double lng = Double.valueOf(coordinates.get(1).toString());
                    System.out.println("lat:" + lat + " lng:" + lng);
                    System.out.println("swLat" + swLat + " swlng: " + swLng + " " + neLat + " " + neLng);
                    if (!(swLat <= lat && lat <= neLat &&
                            swLng <= lng && lng <= neLng)) {
                        insideGermany = false;
                    }
                }

            }else{
                insideGermany = false;
            }

            System.out.println(insideGermany);
            Assert.assertThat(insideGermany, is(true));
//                    double lat = Double.parseDouble(JsonPath.read(s, "$.coordinates[0]"));
//                    double lng = Double.parseDouble(JsonPath.read(s, "$.coordinates[1]"));

            // SWLAT < NELAT
            // SWLNG < NELNG
//                    if(!(swLat <= lat && lat <= neLat &&
//                            swLng <= lng && swLng <= swLng)){
//                        return;
//                    }

        }
    }

    @Test
    public void testJsonParse() throws Exception {
        String json = "{\"filter_level\":\"low\",\"retweeted\":false,\"in_reply_to_screen_name\":\"Linda_Pizzuti\",\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"und\",\"in_reply_to_status_id_str\":null,\"id\":646690652772806656,\"in_reply_to_user_id_str\":\"42694642\",\"timestamp_ms\":\"1443018041209\",\"in_reply_to_status_id\":null,\"created_at\":\"Wed Sep 23 14:20:41 +0000 2015\",\"favorite_count\":0,\"place\":{\"id\":\"6b5d375c346e3be9\",\"bounding_box\":{\"type\":\"Polygon\",\"coordinates\":[[[12.0909912,48.5515315],[12.0909912,51.055648],[18.8599103,51.055648],[18.8599103,48.5515315]]]},\"place_type\":\"country\",\"name\":\"Česká republika\",\"attributes\":{},\"country_code\":\"CZ\",\"url\":\"https://api.twitter.com/1.1/geo/id/6b5d375c346e3be9.json\",\"country\":\"Česká republika\",\"full_name\":\"Česká republika\"},\"coordinates\":null,\"text\":\"@Linda_Pizzuti #KloppForTheKop \\n@RodgersOutClub\",\"contributors\":null,\"geo\":null,\"entities\":{\"trends\":[],\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"KloppForTheKop\",\"indices\":[15,30]}],\"user_mentions\":[{\"id\":42694642,\"name\":\"Linda Pizzuti Henry\",\"indices\":[0,14],\"screen_name\":\"Linda_Pizzuti\",\"id_str\":\"42694642\"},{\"id\":2769262383,\"name\":\"#RodgersOut Club\",\"indices\":[32,47],\"screen_name\":\"RodgersOutClub\",\"id_str\":\"2769262383\"}]},\"source\":\"<a href=\\\"http://twitter.com\\\" rel=\\\"nofollow\\\">Twitter Web Client<\\/a>\",\"favorited\":false,\"in_reply_to_user_id\":42694642,\"retweet_count\":0,\"id_str\":\"646690652772806656\",\"user\":{\"location\":\"Prostějov, Česká republika\",\"default_profile\":false,\"profile_background_tile\":false,\"statuses_count\":3555,\"lang\":\"cs\",\"profile_link_color\":\"DD2E44\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/234377028/1350730288\",\"id\":234377028,\"following\":null,\"protected\":false,\"favourites_count\":1134,\"profile_text_color\":\"333333\",\"verified\":false,\"description\":\"Liverpool FC / Winnipeg Jets/ Jestrabi Prostejov supporter and creator of videos\",\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"Michael Liška\",\"profile_background_color\":\"911A1A\",\"created_at\":\"Wed Jan 05 14:20:35 +0000 2011\",\"default_profile_image\":false,\"followers_count\":553,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/605775688520499201/KyQvN41E_normal.jpg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/546230097/liverpool-fc-01.jpg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/546230097/liverpool-fc-01.jpg\",\"follow_request_sent\":null,\"url\":null,\"utc_offset\":7200,\"time_zone\":\"Prague\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":1999,\"profile_sidebar_fill_color\":\"EFEFEF\",\"screen_name\":\"mikaeLiverpool\",\"id_str\":\"234377028\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/605775688520499201/KyQvN41E_normal.jpg\",\"listed_count\":12,\"is_translator\":false}}\n";


        String json2 = "{\"filter_level\":\"low\",\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"en\",\"in_reply_to_status_id_str\":null,\"id\":646690658061824000,\"in_reply_to_user_id_str\":null,\"timestamp_ms\":\"1443018042470\",\"in_reply_to_status_id\":null,\"created_at\":\"Wed Sep 23 14:20:42 +0000 2015\",\"favorite_count\":0,\"place\":{\"id\":\"9753bd58a1395890\",\"bounding_box\":{\"type\":\"Polygon\",\"coordinates\":[[[13.088304,52.338079],[13.088304,52.675323],[13.760909,52.675323],[13.760909,52.338079]]]},\"place_type\":\"admin\",\"name\":\"Berlin\",\"attributes\":{},\"country_code\":\"DE\",\"url\":\"https://api.twitter.com/1.1/geo/id/9753bd58a1395890.json\",\"country\":\"Deutschland\",\"full_name\":\"Berlin, Deutschland\"},\"coordinates\":null,\"text\":\"Current state: #TARGET_OS_TV all the things\\u2026 #prototyping\",\"contributors\":null,\"geo\":null,\"entities\":{\"trends\":[],\"symbols\":[],\"urls\":[],\"hashtags\":[{\"text\":\"TARGET_OS_TV\",\"indices\":[15,28]},{\"text\":\"prototyping\",\"indices\":[45,57]}],\"user_mentions\":[]},\"source\":\"<a href=\\\"http://tapbots.com/tweetbot\\\" rel=\\\"nofollow\\\">Tweetbot for iΟS<\\/a>\",\"favorited\":false,\"in_reply_to_user_id\":null,\"retweet_count\":0,\"id_str\":\"646690658061824000\",\"user\":{\"location\":\"Germany, Berlin\",\"default_profile\":false,\"profile_background_tile\":true,\"statuses_count\":1788,\"lang\":\"de\",\"profile_link_color\":\"CC3366\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/173381634/1432889350\",\"id\":173381634,\"following\":null,\"protected\":false,\"favourites_count\":137,\"profile_text_color\":\"333333\",\"verified\":false,\"description\":\"Coder @ #iOS, #Mac, #Web, ^(.*)$ - excited to code and cook for @1KitchenStories\",\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"Oliver Michalak\",\"profile_background_color\":\"F0F0F0\",\"created_at\":\"Sun Aug 01 09:11:57 +0000 2010\",\"default_profile_image\":false,\"followers_count\":130,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/378800000514214553/77a004c78252b290ea5899cce6adf4a7_normal.jpeg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/604207420374650880/r8no9Kvu.jpg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/604207420374650880/r8no9Kvu.jpg\",\"follow_request_sent\":null,\"url\":\"http://oliver.werk01.de\",\"utc_offset\":10800,\"time_zone\":\"Athens\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":122,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"omichde\",\"id_str\":\"173381634\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/378800000514214553/77a004c78252b290ea5899cce6adf4a7_normal.jpeg\",\"listed_count\":15,\"is_translator\":false}}\n";


        String jsonWithoutLocation = "{\"filter_level\":\"low\",\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"en\",\"in_reply_to_status_id_str\":null,\"id\":646690658967810048,\"extended_entities\":{\"media\":[{\"sizes\":{\"thumb\":{\"w\":150,\"resize\":\"crop\",\"h\":150},\"small\":{\"w\":340,\"resize\":\"fit\",\"h\":340},\"medium\":{\"w\":512,\"resize\":\"fit\",\"h\":512},\"large\":{\"w\":512,\"resize\":\"fit\",\"h\":512}},\"id\":646690657956950016,\"media_url_https\":\"https://pbs.twimg.com/media/CPmBuiMWcAAct1_.png\",\"media_url\":\"http://pbs.twimg.com/media/CPmBuiMWcAAct1_.png\",\"expanded_url\":\"http://twitter.com/NOSapps/status/646690658967810048/photo/1\",\"indices\":[120,142],\"id_str\":\"646690657956950016\",\"type\":\"photo\",\"display_url\":\"pic.twitter.com/VPHM67L4Ap\",\"url\":\"http://t.co/VPHM67L4Ap\"}]},\"in_reply_to_user_id_str\":null,\"timestamp_ms\":\"1443018042686\",\"in_reply_to_status_id\":null,\"created_at\":\"Wed Sep 23 14:20:42 +0000 2015\",\"favorite_count\":0,\"place\":{\"id\":\"3078869807f9dd36\",\"bounding_box\":{\"type\":\"Polygon\",\"coordinates\":[[[13.088304,52.338079],[13.088304,52.675323],[13.760909,52.675323],[13.760909,52.338079]]]},\"place_type\":\"city\",\"name\":\"Berlin\",\"attributes\":{},\"country_code\":\"DE\",\"url\":\"https://api.twitter.com/1.1/geo/id/3078869807f9dd36.json\",\"country\":\"Deutschland\",\"full_name\":\"Berlin, Germany\"},\"coordinates\":null,\"text\":\"The most #secure #messenger ever lands on the @AppStore ! \\nMore info &amp; #freedownload link at http://t.co/OjFFEehJ3a http://t.co/VPHM67L4Ap\",\"contributors\":null,\"geo\":null,\"entities\":{\"trends\":[],\"symbols\":[],\"urls\":[{\"expanded_url\":\"http://goo.gl/AfxNCc\",\"indices\":[97,119],\"display_url\":\"goo.gl/AfxNCc\",\"url\":\"http://t.co/OjFFEehJ3a\"}],\"hashtags\":[{\"text\":\"secure\",\"indices\":[9,16]},{\"text\":\"messenger\",\"indices\":[17,27]},{\"text\":\"freedownload\",\"indices\":[75,88]}],\"media\":[{\"sizes\":{\"thumb\":{\"w\":150,\"resize\":\"crop\",\"h\":150},\"small\":{\"w\":340,\"resize\":\"fit\",\"h\":340},\"medium\":{\"w\":512,\"resize\":\"fit\",\"h\":512},\"large\":{\"w\":512,\"resize\":\"fit\",\"h\":512}},\"id\":646690657956950016,\"media_url_https\":\"https://pbs.twimg.com/media/CPmBuiMWcAAct1_.png\",\"media_url\":\"http://pbs.twimg.com/media/CPmBuiMWcAAct1_.png\",\"expanded_url\":\"http://twitter.com/NOSapps/status/646690658967810048/photo/1\",\"indices\":[120,142],\"id_str\":\"646690657956950016\",\"type\":\"photo\",\"display_url\":\"pic.twitter.com/VPHM67L4Ap\",\"url\":\"http://t.co/VPHM67L4Ap\"}],\"user_mentions\":[{\"id\":74594552,\"name\":\"App Store \",\"indices\":[46,55],\"screen_name\":\"AppStore\",\"id_str\":\"74594552\"}]},\"source\":\"<a href=\\\"http://twitter.com\\\" rel=\\\"nofollow\\\">Twitter Web Client<\\/a>\",\"favorited\":false,\"in_reply_to_user_id\":null,\"retweet_count\":0,\"id_str\":\"646690658967810048\",\"user\":{\"location\":\"Worldwide\",\"default_profile\":false,\"profile_background_tile\":false,\"statuses_count\":294,\"lang\":\"en\",\"profile_link_color\":\"F1503F\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/775462866/1434457469\",\"id\":775462866,\"following\":null,\"protected\":false,\"favourites_count\":62,\"profile_text_color\":\"333333\",\"verified\":false,\"description\":\"We create fun, innovative and useful mobile apps that are suitable for a wide variety of users.\",\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\"NOS Apps\",\"profile_background_color\":\"DBDBDB\",\"created_at\":\"Thu Aug 23 08:14:27 +0000 2012\",\"default_profile_image\":false,\"followers_count\":703,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/530270499977195520/78dGyfdY_normal.png\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/469429984713392129/JSpHG0So.png\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/469429984713392129/JSpHG0So.png\",\"follow_request_sent\":null,\"url\":\"http://www.nosapps.com\",\"utc_offset\":3600,\"time_zone\":\"London\",\"notifications\":null,\"profile_use_background_image\":false,\"friends_count\":1471,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"NOSapps\",\"id_str\":\"775462866\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/530270499977195520/78dGyfdY_normal.png\",\"listed_count\":10,\"is_translator\":false}}\n";


        String jsonWithLocation = "{\"filter_level\":\"low\",\"retweeted\":false,\"in_reply_to_screen_name\":null,\"possibly_sensitive\":false,\"truncated\":false,\"lang\":\"en\",\"in_reply_to_status_id_str\":null,\"id\":646691060148781057,\"in_reply_to_user_id_str\":null,\"timestamp_ms\":\"1443018138335\",\"in_reply_to_status_id\":null,\"created_at\":\"Wed Sep 23 14:22:18 +0000 2015\",\"favorite_count\":0,\"place\":{\"id\":\"37439688c6302728\",\"bounding_box\":{\"type\":\"Polygon\",\"coordinates\":[[[11.360589,48.061634],[11.360589,48.248124],[11.722918,48.248124],[11.722918,48.061634]]]},\"place_type\":\"city\",\"name\":\"Munich\",\"attributes\":{},\"country_code\":\"DE\",\"url\":\"https://api.twitter.com/1.1/geo/id/37439688c6302728.json\",\"country\":\"Deutschland\",\"full_name\":\"Munich, Bavaria\"},\"coordinates\":{\"type\":\"Point\",\"coordinates\":[11.57412,48.14814]},\"text\":\"#cytwombly @ Museum Brandhorst https://t.co/YvsJhkeru0\",\"contributors\":null,\"geo\":{\"type\":\"Point\",\"coordinates\":[48.14814,11.57412]},\"entities\":{\"trends\":[],\"symbols\":[],\"urls\":[{\"expanded_url\":\"https://instagram.com/p/7-e6AblU5X/\",\"indices\":[31,54],\"display_url\":\"instagram.com/p/7-e6AblU5X/\",\"url\":\"https://t.co/YvsJhkeru0\"}],\"hashtags\":[{\"text\":\"cytwombly\",\"indices\":[0,10]}],\"user_mentions\":[]},\"source\":\"<a href=\\\"http://instagram.com\\\" rel=\\\"nofollow\\\">Instagram<\\/a>\",\"favorited\":false,\"in_reply_to_user_id\":null,\"retweet_count\":0,\"id_str\":\"646691060148781057\",\"user\":{\"location\":\"\",\"default_profile\":false,\"profile_background_tile\":true,\"statuses_count\":113166,\"lang\":\"en\",\"profile_link_color\":\"999698\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/440252035/1438364469\",\"id\":440252035,\"following\":null,\"protected\":false,\"favourites_count\":94175,\"profile_text_color\":\"333333\",\"verified\":false,\"description\":\"she broke up with me i have small eyes\",\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"000000\",\"name\":\"gland\",\"profile_background_color\":\"FFFFFF\",\"created_at\":\"Sun Dec 18 19:15:24 +0000 2011\",\"default_profile_image\":false,\"followers_count\":333,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/627172155025981440/K7jb8qtD_normal.jpg\",\"geo_enabled\":true,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/498641873292886016/3mFDKX3n.jpeg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/498641873292886016/3mFDKX3n.jpeg\",\"follow_request_sent\":null,\"url\":\"http://csection.tumblr.com\",\"utc_offset\":10800,\"time_zone\":\"Bucharest\",\"notifications\":null,\"profile_use_background_image\":true,\"friends_count\":161,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"wronghat\",\"id_str\":\"440252035\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/627172155025981440/K7jb8qtD_normal.jpg\",\"listed_count\":7,\"is_translator\":false}}\n";

        Configuration conf2 = Configuration.defaultConfiguration();
        Configuration conf = conf2.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

        //Works fine
//        JSONArray lat = JsonPath.using(conf).parse(jsonWithLocation).read("$['geo']['coordinates']");
        LinkedHashMap exists = JsonPath.using(conf).parse(jsonWithLocation).read("$['geo']");
        //double lng = JsonPath.using(conf).parse(jsonWithLocation).read("$['geo']['coordinates'][1]");


        System.out.println(exists);

        System.out.println(exists.get("coordinates"));

        Assert.assertTrue(exists.get("coordinates") != null);
        System.out.println(JsonPath.parse(exists.get("coordinates")).read("[0]"));
        Assert.assertTrue(exists.get("coordinates") != null);

        Assert.assertThat(exists.containsValue("bla"),is(true));
        LinkedHashMap shouldnotexist = JsonPath.using(conf).parse(jsonWithoutLocation).read("$['geo']");

        System.out.println(shouldnotexist);
        Assert.assertTrue(shouldnotexist == null);
    }
}