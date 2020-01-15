package com.example.oblroomreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UserReservationsActivity extends AppCompatActivity {
    private String url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/user/";
    private String userId;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("gestDecURes", "onFling");
                boolean onSwipe = e1.getX() < e2.getX();
                if (onSwipe) {
                    Log.d("gestDecURes2", "onSwipe");
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadTask task = new ReadTask();
        task.execute(url + userId);
    }

    private class ReadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            Log.d("BackgroundURes", "doInBackground");
            String url = urls[0];
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            Request request = builder.build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()){
                    ResponseBody responseBody = response.body();
                    return responseBody.string();
                }else{
                    cancel(true);
                    return url + "\n" + response.code() + " " + response.message();
                }
            } catch (IOException ex) {
                Log.d("UserResInBackCatch", ex.getMessage());
                cancel(true);
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            Log.d("UserResPostExec", "PostExec ");
            super.onPostExecute(jsonString);
            ProgressBar progressBar = findViewById(R.id.UserResProgBar);
            progressBar.setVisibility(View.GONE);
            populateList(jsonString);
        }

        @Override
        protected void onCancelled(String Message) {
            Log.d("UserResOnCancel", "onCancelled");
            ProgressBar progressBar = findViewById(R.id.UserResProgBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void populateList(String jsonString){
        Gson gson = new GsonBuilder().create();
        JsonReservation[] reservations = gson.fromJson(jsonString, JsonReservation[].class);
        ListView listView = findViewById(R.id.UserResListView);
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, reservations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), FocusedReservationActivity.class);
                JsonReservation reservation = (JsonReservation) parent.getItemAtPosition(position);
                intent.putExtra(FocusedReservationActivity.Companion.getFOCUSRES(), reservation);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
