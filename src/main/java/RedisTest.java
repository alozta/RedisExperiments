import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by alozta on 8/17/16.
 */
public class RedisTest {

    public static void main(String[] args) {

        Random r = new Random();
        for(int i=0; i<5; ++i){
            //                          MMSI                                        LAT         LONG
            new VesselEntry((new Integer(r.nextInt(100000000)+1)).toString() + " 29.454563 44.332134" );      //random mmsi's
        }

        System.out.println(VesselEntry.getReverseRangeByScore(Double.MAX_VALUE, 0));
        System.out.println(VesselEntry.getLastNMinutes(6));
        for(String s : VesselEntry.getLastNMinutes(6)){
            System.out.println(VesselEntry.hGetAll(s));
        }

    }
}
