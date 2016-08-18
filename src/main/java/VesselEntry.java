import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by alozta on 8/17/16.
 */
public class VesselEntry extends Entry {

    //**************************************************************************************

    //**************************************************************************************

    //**************************************************************************************
    //Redis configurations
    static String SORTED_SET_KEY = "mmsi";
    static String IP="localhost";
    static Jedis JEDIS = new Jedis(IP);
    //**************************************************************************************


    //***************************************************************************************
    //REDIS COMMAND EQUIVALENTS
    /**
     * ZADD
     * */
    public VesselEntry(String value){
        super(value);
        //                                                                                  SECONDS
        JEDIS.zadd(SORTED_SET_KEY, (int)Math.round(Double.parseDouble(""+new Date().getTime())/1000), getMMSI());     //ADD ID TO SORTED SET
        Map<String,String> map = new HashMap<String,String>();
        map.put("lat", getLat());
        map.put("lon", getLon());                                                               //GET ID PROPS INTO MAP
        //additional properties
        JEDIS.hmset(getMMSI(), map);                                                            //STORE ID & PROPS IN HASH
    }

    public static Set<String> getLastNMinutes(int N){
        int time=(int)Math.round(Double.parseDouble(""+new Date().getTime())/1000);
        //                                        now in seconds, N minutes before
        return JEDIS.zrevrangeByScore(SORTED_SET_KEY, time, time-60*N);
    }

    public static Map<String, String> hGetAll(String id){
        return JEDIS.hgetAll(id);
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
    public static Set<String> getReverseRangeByScore(double max, double min) {
        return JEDIS.zrevrangeByScore(SORTED_SET_KEY,max,min);
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
