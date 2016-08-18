import java.util.*;

/**
 * Created by alozta on 8/17/16.
 */
public abstract class Entry {
    List<String> values = new ArrayList<String>();

    public Entry(String properties){

        String [] args = properties.split(" ");
        for(String arg : args){
            values.add(arg);        //order and number of elements are important
        }
    }

    /**
     * @return Input values combined.
     * */
    public String getFields(){
        String p="";
        for(String s : values){
            p += s + " ";
        }
        return p.trim();
    }

    /**
     * @return MMSI id
     * */
    public String getMMSI(){
        return values.get(0);
    }

    /**
     *
     * */
    public String getLat(){
        return values.get(1);
    }

    /**
     *
     * */
    public String getLon(){
        return values.get(2);
    }

    //*************************ENTRY MANIPULATION METHODS HERE************************
    //********************************************************************************
    //getLocation, getTime, etc.
}
