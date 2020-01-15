package com.example.oblroomreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RoomActivity extends AppCompatActivity {
    private String url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/";
    public static final String ROOM = "ROOM";
    private JsonRoom room;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        room = (JsonRoom) intent.getSerializableExtra(ROOM);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("gestDecRoomAct", "onFling");
                boolean onSwipe = e1.getX() < e2.getX();
                if (onSwipe) {
                    Log.d("gestDecRoomAct2", "onSwipe");
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
        task.execute(url + room.getId());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Log.d(TAG, "onTuch: " + event);
        // boolean eventHandlingFinished = true;
        //return eventHandlingFinished;
        return gestureDetector.onTouchEvent(event);
    }

    private class ReadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            Log.d("ReadTaskTag", "This Went Wrong");
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
                } else {
                    cancel(true);
                    return url + "/n" + response.code() + " " + response.message();
                }
            } catch (IOException ex) {
                Log.d("ReadTaskTag", ex.getMessage());
                cancel(true);
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString2) {
            Log.d("PostExecOR", "PostExecute error");
            super.onPostExecute(jsonString2);
            ProgressBar progressBar = findViewById(R.id.ReservationProgBar);
            progressBar.setVisibility(View.GONE);
            populateList(jsonString2);

        }

        /*@Override
        protected void onCancelled(String Message) {
            Log.d("CancelledOR", "OnCancelled");
            ProgressBar progressBar = findViewById(R.id.ReservationProgBar);
            progressBar.setVisibility(View.GONE);
        }*/
    }

    private void populateList (String jsonString){
        Gson gson = new GsonBuilder().create();
        JsonReservation[] room = gson.fromJson(jsonString, JsonReservation[].class);
        ListView listview = findViewById(R.id.roomListView);
        //ReservationAdapter adapter = new ReservationAdapter(getBaseContext(), R.layout.reservation_adapter, rooms);
        ArrayAdapter<JsonReservation> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, room);
        listview.setAdapter(adapter);
    }

    public void AddNewRes(View view){
        Intent intent = new Intent (RoomActivity.this, AddReservation.class);
        intent.putExtra(AddReservation.ADDRES, room);
        startActivity(intent);
    }
}
