import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by alozta on 8/17/16.
 */
public class RedisConnection {

    private static RedisConnection ve;

    //**************************************************************************************
    //Redis configurations
    private static String SORTED_SET_KEY = "mmsi";
    private static String IP="localhost";
    private static Jedis JEDIS = new Jedis(IP);
    //**************************************************************************************

    /**
     * SINGLETON CONSTRUCTOR
     * */
    public static RedisConnection getInstance(){
        if(ve==null){
            ve = new RedisConnection();
        }
        return ve;
    }

    /**
     * Adds entry both SORTED SET and the SET
     *
     * @param query Redis add query, key field value, THE METHOD EXPECTS ONLY ONE FIELD AND ONE VALUE
     * */
    public void add(String query){
        if(query.split(" ").length!=3){
            System.out.println("ILLEGAL INPUT: " + query);
            return;
        }

        //      1-)the most up-to-date key time will be held in sorted set                  SECONDS
        JEDIS.zadd(SORTED_SET_KEY, (int)Math.round(Double.parseDouble(""+new Date().getTime())/1000), getMMSI(query));     //ADD ID TO SORTED SET

        /* MULTIPLE FIELD INPUT HERE
        Map<String,String> map = new HashMap<String,String>();
        map.put("lat", getLat(value));
        map.put("lon", getLon(value));                                                               //GET ID PROPS INTO MAP
        //additional properties
        //...
        JEDIS.hmset(getMMSI(value), map);                                                            //STORE ID & PROPS IN HASH
        */

        //SINGLE FIELD INPUT
        //2-) the rest will be held in hash
        JEDIS.hset(getMMSI(query), getField(query), getValue(query));
    }

    public RedisConnection(){}


    //***************************************************************************************
    //It's assumed it's in this order. ORDER CAN BE CHANGED AND MANIPULATED
    /**
     * @return MMSI id
     * */
    private String getMMSI(String value){
        return value.split(" ")[0];
    }

    /**
     *
     * */
    private String getField(String value){
        return value.split(" ")[1];
    }

    /**
     *
     * */
    private String getValue(String value){
        return value.split(" ")[2];
    }
    //***************************************************************************************


    //***************************************************************************************
    //REDIS COMMAND EQUIVALENTS, some of them is not used yet

    /**
     * Deletes hash fields
     *
     * @param key Hash key
     * @param args Field names
     * */
    public static void delete(String key, String ... args){
        JEDIS.hdel(key, args);
    }

    /**
     * Returns last N minutes of input
     *
     * @param N Minutes
     * */
    public static Set<String> getLastNMinutes(int N){
        int time=(int)Math.round(Double.parseDouble(""+new Date().getTime())/1000);
        //                                        now in seconds, N minutes before
        return JEDIS.zrevrangeByScore(SORTED_SET_KEY, time, time-60*N);
    }

    /**
     * Returns all hash fields and values
     *
     * @param id Hash key
     * */
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

    /**
     * Closes connections
     * */
    public static void close(){
        JEDIS.close();
        ve=null;
    }

    //*************************************************************************
    //Mutator methods for local variables
    public static void setIP(String IP) {
        RedisConnection.IP = IP;
    }

    public static void setSortedSetKey(String sortedSetKey) {
        RedisConnection.SORTED_SET_KEY = sortedSetKey;
    }
    //*************************************************************************
}
