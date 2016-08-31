import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by alozta on 8/17/16.
 */
public class iRedis {

    private static iRedis ve;

    //**************************************************************************************
    //Redis configurations
    private static String SORTED_SET_KEY = "mmsi";
    private static String IP="localhost";
    private static Jedis JEDIS = new Jedis(IP);
    private static List<String> CHANNELS = new ArrayList<String>(){{add("redisChannel");}};
    private static CountDownLatch messageReceivedLatch = new CountDownLatch(1);
    private static CountDownLatch publishLatch = new CountDownLatch(1);
    private static List<String> messageContainer = new ArrayList<String>();
    //**************************************************************************************

    /**
     * SINGLETON CONSTRUCTOR
     * */
    public static iRedis getInstance(){
        if(ve==null){
            ve = new iRedis();
        }
        return ve;
    }

    //***************************************************************************************
    //REDIS PUBSUB

    /**
     * Redis publisher
     * Redis equivalent: PUBLISH CHANNEL MESSAGE
     *
     * @param channel Channel name to publish
     * @param msg Message to be published
     * */
    public void publish(final String channel, final String msg) {
        /*log("Connecting");
        log("Waiting to publish");
        log("Ready to publish, waiting one sec");*/
        log("publishing " + msg + " on channel " + channel);
        JEDIS.publish(channel, msg);
        // publish away!
        publishLatch.countDown();
        //log("published, closing publishing connection");
        JEDIS.quit();
        //log("publishing connection closed");
    }

    /**
     * Runs subscriber as a thread because of its io blocking status.
     * */
    public void subscribeThread(final String ... args){
        new Thread(new Runnable() {
            public void run() {
                try {
                    //JEDIS.subscribe(jedisPubSub, args);
                    //JEDIS.quit();
                    subscribe(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "subscriberThread").start();
    }

    /**
     * Redis subscriber
     * Redis equivalent: SUBSCRIBE CHANNELS[...]
     *
     * @param args Channels to be subscribed
     * post: Holds the messages in the List named messageContainer.
     * */
    private JedisPubSub subscribe( String ... args) {
        final JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                log("onUnsubscribe");
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                log("onSubscribe");
            }

            @Override
            public void onPUnsubscribe(String pattern, int subscribedChannels) {
            }

            @Override
            public void onPSubscribe(String pattern, int subscribedChannels) {
            }

            @Override
            public void onPMessage(String pattern, String channel, String message) {
            }

            @Override
            public void onMessage(String channel, String message) {
                messageContainer.add(message);
                log("Message received: "+ message);
                //log("Messages: "+ messageContainer.toString());
                messageReceivedLatch.countDown();
            }
        };

        if(ve==null){
            System.out.println("Thread exited.");
            System.exit(1);
        }else {
            //log("Connecting");
            //log("subscribing");
            try {
                JEDIS.subscribe(jedisPubSub, args);
            } catch (redis.clients.jedis.exceptions.JedisConnectionException e){
                //e.printStackTrace();
                System.out.println("Thread exited.");
                System.exit(1);
            }
            //log("subscribe returned, closing down");
            JEDIS.quit();
        }

        return jedisPubSub;
    }

    static final long startMillis = System.currentTimeMillis();     //System up-time in ms

    /**
     * Logs the strings with system up-time.
     * */
    private static void log(String string, Object... args) {
        long millisSinceStart = System.currentTimeMillis() - startMillis;
        System.out.printf("%20s %6d %s\n", Thread.currentThread().getName(), millisSinceStart,
                String.format(string, args));
    }
    //***************************************************************************************

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

    /**
     * @param field Field of hash
     * @param key Key of hash
     * @param value Value of hash
     * */
    public void add(String key, String field, String value){
        JEDIS.zadd(SORTED_SET_KEY, (int)Math.round(Double.parseDouble(""+new Date().getTime())/1000), key);
        JEDIS.hset(key, field, value);
    }

    private iRedis(){}


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
        iRedis.IP = IP;
    }

    public static void setSortedSetKey(String sortedSetKey) {
        iRedis.SORTED_SET_KEY = sortedSetKey;
    }

    public static List<String> getCHANNELS() {
        return CHANNELS;
    }

    public static void addChannel(String channel){
        CHANNELS.add(channel);
    }

    public static void changeChannelName(int i, String newChannelName){
        CHANNELS.add(i,newChannelName);
    }

    public static void removeChannel(Object o){
        CHANNELS.remove(o);
    }

    public static List<String> getMessageContainer() {
        List<String> list = messageContainer;
        messageContainer=new ArrayList<String>();
        return list;
    }

    //*************************************************************************
}
