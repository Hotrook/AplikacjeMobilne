package frost.chucknorrisandcats;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by seba on 26.05.2017.
 */

public class Response {
    private String type;
    private Value value;

    public Value getValue() {
        return value;
    }

    public static Response parseJSON( JsonObject response ){
        Gson gson = new Gson();
        Response res = gson.fromJson( response, Response.class );
        return res;
    }
}
