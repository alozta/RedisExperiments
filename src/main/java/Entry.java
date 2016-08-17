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

    //*************************ENTRY MANIPULATION METHODS HERE************************
    //********************************************************************************
    //getLocation, getTime, etc.
}
