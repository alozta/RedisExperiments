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

        iRedis ve = iRedis.getInstance();

        /*Random r = new Random();
        for(int i=1; i<6; ++i){
            Integer key = new Integer(r.nextInt(100000000) + 1);
            for(int j=1;j<10;++j){
                if(i*j%5!=0) {              //TEST: leave some fields un-filled
                    ve.add(key.toString() + " field" + j + " " + (new Integer(r.nextInt(100) + 1)).toString());      //add <key> <field> <value>
                }
            }
        }*/

        //ve.publish(RedisConnection.getCHANNELS().get(0), "hello from intelliJ");
        ve.subscribeThread(iRedis.getCHANNELS().toArray(new String[iRedis.getCHANNELS().size()]));

        /*System.out.println("List all (high score to low): " + RedisConnection.getReverseRangeByScore(Double.MAX_VALUE, 0));
        System.out.println("Last N minutes: " + RedisConnection.getLastNMinutes(6));
        for(String s : RedisConnection.getLastNMinutes(6)){
            System.out.println(RedisConnection.hGetAll(s));
        }*/

        //test for thread input
        for(int i=1; i<3; ++i) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(iRedis.getMessageContainer());
        }
        iRedis.close();
        System.out.println("main thread ended.");
    }
}
