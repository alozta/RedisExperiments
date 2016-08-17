import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;

/**
 * Created by alozta on 8/17/16.
 */
public class VesselEntry extends Entry {

    static String SORTED_SET_KEY = "vessels";
    static String IP="localhost";
    static Jedis JEDIS = new Jedis(IP);


    //***************************************************************************************
    //REDIS COMMAND EQUIVALENTS
    /**
     * ZADD
     * */
    public VesselEntry(String value){
        super(value);
        JEDIS.zadd(SORTED_SET_KEY, Double.parseDouble(""+new Date().getTime()), getFields());
    }

    /**
     * Remove by rank
     *
     * ZREMRANGEBYRANK
     * */
    public static void removeRangeByRank(long min, long max){
        JEDIS.zremrangeByRank(SORTED_SET_KEY, min, max);
    }

    /**
     * Remove by score
     *
     * ZREMRANGEBYSCORE
     * */
    public static void removeRangeByScore(double min, double max){
        JEDIS.zremrangeByScore(SORTED_SET_KEY, min, max);
    }

    /**
     * Removes the element of the set
     *
     * ZREM
     * */
    public static void removeElementByNo(String value){
        JEDIS.zrem(SORTED_SET_KEY, value);
    }

    /**
     * List by score
     *
     * ZRANGEBYSCORE
     * */
    public static Set<String> getRangeByScore(double min, double max){
        return JEDIS.zrangeByScore(SORTED_SET_KEY,min,max);
    }

    /**
     * Reverse list by score
     *
     * ZREVRANGEBYSCORE
     * */
    public static Set<String> getReverseRangeByScore(double min, double max) {
        return JEDIS.zrevrangeByScore(SORTED_SET_KEY,min,max);
    }
    //*************************************************************************

    //*************************************************************************
    //Mutator methods for local variables
    public static void setIP(String IP) {
        VesselEntry.IP = IP;
    }

    public static void setSortedSetKey(String sortedSetKey) {
        VesselEntry.SORTED_SET_KEY = sortedSetKey;
    }
    //*************************************************************************
}
