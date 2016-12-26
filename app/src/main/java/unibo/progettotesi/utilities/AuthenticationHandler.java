package unibo.progettotesi.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import unibo.progettotesi.activities.MainActivity;
import unibo.progettotesi.json.createProfileRequest.Content;
import unibo.progettotesi.json.createProfileRequest.Description;
import unibo.progettotesi.json.createProfileRequest.Name;
import unibo.progettotesi.json.createProfileRequest.Request;
import unibo.progettotesi.json.createProfileResponse.Response;

/**
 * Created by francescofiacco on 16/11/2016.
 */

public class AuthenticationHandler {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void createAccount() {
        Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
                .baseUrl(Constants.AUTHENTICATION_HANDLER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())	//parse Gson string
                .build();

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(Constants.AUTHENTICATION_HANDLER_BASE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout( 60000 );
            //Log.wtf("TIMEOUT", connection.getConnectTimeout() + "\t" + connection.getReadTimeout());
        } catch (Exception e) {
            e.printStackTrace();
        }



        CreateProfile service = retrofit.create(CreateProfile.class);

        ArrayList<String> list = new ArrayList<>();
        list.add("string");

        Call<Response> queryResponseCall = service.createProfile(new Request("user", "", "", "mobility", "666", new Name("yeah"), new Description("string"), list, new Content("wow")));

        queryResponseCall.enqueue(new Callback<Response>(){

            @Override
            public void onResponse(retrofit2.Response<Response> response) {

                try{
                    if(response.body() != null && response.code() == 200){
                        MainActivity.setAccountCreated(true);
                    }else if(response.body() == null && response.code() == 200){
                        MainActivity.setAccountCreated(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface CreateProfile {
        @POST("extprofile/me/mobility")

        Call<Response> createProfile(@Body Request request);
    }
}
