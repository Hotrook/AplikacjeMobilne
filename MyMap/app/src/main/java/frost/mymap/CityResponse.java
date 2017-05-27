package frost.mymap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seba on 25.05.2017.
 */

public class CityResponse {

//    @SerializedName("movies")
    List<City> cities;

    public CityResponse() {
        cities = new ArrayList<City>();
    }

    public static CityResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CityResponse boxOfficeMovieResponse = gson.fromJson(response, CityResponse.class);
        return boxOfficeMovieResponse;
    }

}
