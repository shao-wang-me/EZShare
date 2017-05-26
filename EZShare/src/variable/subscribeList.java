package variable;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class can create a list of JSON objects, which contain
 * id and resource template
 *
 * Created by qiuyankun on 15/5/17.
 */
public class subscribeList {

    private  volatile ArrayList<JSONObject> subList;

    public  subscribeList() {
        this.subList = new ArrayList<JSONObject>();
    }

    public synchronized void add(String userID, String actualID, boolean relay, Resource resource) {
        JSONObject temp = new JSONObject();
        temp.put("userID",userID);
        temp.put("actualID",actualID);
        temp.put("relay",relay);
        temp.put("resourceTemplate",resource);
        this.subList.add(temp);
    }

    public synchronized void add(JSONObject j) {
        this.subList.add(j);
    }

    public synchronized void remove(String actualID) {
        for (JSONObject temp : subList) {
            if (temp.getString("actualID").equals(actualID)) {
                this.subList.remove(temp);
            }
        }
    }

    public boolean contain(JSONObject j) {
        for (JSONObject temp : this.subList) {
            if (j.getString("actualID").equals(temp.getString("actualID"))) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<JSONObject> getList(){
        return subList;
    }

    public int size() {
        return this.subList.size();
    }

    public ArrayList<JSONObject> getSubList(){
        return subList;
    }


}