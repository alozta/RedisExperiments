import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

/**
 * Created by alozta on 8/17/16.
 */
public class RedisTest {

    public static void main(String[] args) {

        VesselEntry ve1 = new VesselEntry("antirez 1977 1");
        VesselEntry ve2 = new VesselEntry("ramirez 1970 0");

        System.out.println(VesselEntry.getRangeByScore(0, Double.MAX_VALUE));
    }
}
