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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ShowRoomsActivity extends AppCompatActivity {
    private String url = "http://anbo-roomreservationv3.azurewebsites.net/api/Rooms";
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rooms);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("gestDecShowRooms", "onFling");
                boolean onSwipe = e1.getX() < e2.getX();
                if (onSwipe) {
                    Log.d("gestDecShowRooms2", "onSwipe");
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
        task.execute(url);
    }

    void getDataUsingOkHttpEnqueue(){
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String jsonString = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateList(jsonString);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView messageView = findViewById(R.id.ShowRoomsTxt);
                        messageView.setText(ex.getMessage());
                    }
                });
            }
        });
    }

    class ReadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);
            Request request = requestBuilder.build();
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
                cancel(true);
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            ProgressBar progressBar = findViewById(R.id.ShowRoomsProg);
            progressBar.setVisibility(View.GONE);
            populateList(jsonString);
        }

        @Override
        protected void onCancelled(String message) {
            ProgressBar progressBar = findViewById(R.id.ShowRoomsProg);
            progressBar.setVisibility(View.GONE);
            Log.d("ShowRoomsOnCancel", message);
        }
    }

    private void populateList(String jsonString){
        Gson gson = new GsonBuilder().create();
        JsonRoom[] rooms = gson.fromJson(jsonString, JsonRoom[].class);
        ListView listView = findViewById(R.id.RoomList);
        ArrayAdapter<JsonRoom> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, rooms);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RoomActivity.class);
                JsonRoom room = (JsonRoom) parent.getItemAtPosition(position);
                intent.putExtra(RoomActivity.ROOM, room);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
