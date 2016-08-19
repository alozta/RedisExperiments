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

        RedisConnection ve = RedisConnection.getInstance();

        Random r = new Random();
        for(int i=1; i<6; ++i){
            Integer key = new Integer(r.nextInt(100000000) + 1);
            for(int j=1;j<10;++j){
                if(i*j%5!=0) {              //TEST: leave some fields un-filled
                    ve.add(key.toString() + " field" + j + " " + (new Integer(r.nextInt(100) + 1)).toString());      //add <key> <field> <value>
                }
            }
        }

        System.out.println("List all (high score to low): " + RedisConnection.getReverseRangeByScore(Double.MAX_VALUE, 0));
        System.out.println("Last N minutes: " + RedisConnection.getLastNMinutes(6));
        for(String s : RedisConnection.getLastNMinutes(6)){
            System.out.println(RedisConnection.hGetAll(s));
        }

        RedisConnection.close();
    }
}
