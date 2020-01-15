package com.example.oblroomreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddReservation extends AppCompatActivity {

    private GestureDetector gestureDetector;

    public static String ADDRES = "ADDRES";
    private String userId;
    private JsonRoom oneRes;
    private Calendar fromCal = Calendar.getInstance();
    private Calendar toCal = Calendar.getInstance();

    private Button datePickFTimeBtn;
    private Button datePickTTimeBtn;
    private Button datePickFDateBtn;
    private Button datePickTDateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);
        Intent intent = getIntent();
        oneRes = (JsonRoom) intent.getSerializableExtra(ADDRES);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        TextView RoomName = findViewById(R.id.RoomNameTxtView);
        RoomName.setText("Making reservation for room " + oneRes.getName());
        datePickFTimeBtn = findViewById(R.id.TimePickerfromBtn);
        datePickTTimeBtn = findViewById(R.id.TimePickertoBtn);
        datePickFDateBtn = findViewById(R.id.DatePickerfromBtn);
        datePickTDateBtn = findViewById(R.id.DatePickertoBtn);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("gestDecAddRes", "onFling");
                boolean onSwipe = e1.getX() < e2.getX();
                if (onSwipe) {
                    Log.d("gestDecAddRes2", "onSwipe");
                    finish();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void tDateBtnClicked(View view){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                toCal.set(Calendar.YEAR, year);
                toCal.set(Calendar.MONTH, month);
                toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                DateFormat dateFormat = DateFormat.getDateInstance();
                String dateString = dateFormat.format(toCal.getTimeInMillis());
                datePickTDateBtn.setText(dateString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, currentYear, currentMonth, currentDayOfMonth);
        dialog.show();
    }

    public void fDateBtnClicked(View view){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                fromCal.set(Calendar.YEAR, year);
                fromCal.set(Calendar.MONTH, month);
                fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                DateFormat dateFormat = DateFormat.getDateInstance();
                String dateString = dateFormat.format(fromCal.getTimeInMillis());
                datePickFDateBtn.setText(dateString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, currentYear, currentMonth, currentDayOfMonth);
        dialog.show();
    }

    public void tTimeBtnClicked(View view){
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                toCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                toCal.set(Calendar.MINUTE, minute);
                DateFormat dF = DateFormat.getTimeInstance();
                String timeString = dF.format(toCal.getTimeInMillis());
                datePickTTimeBtn.setText(timeString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(this, timeSetListener, curHour, curMin, true);
        dialog.show();
    }

    public void fTimeBtnClicked(View view){
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                fromCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                fromCal.set(Calendar.MINUTE, minute);
                DateFormat dateFormat = DateFormat.getTimeInstance();
                String timeString = dateFormat.format(fromCal.getTimeInMillis());
                datePickFTimeBtn.setText(timeString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(this, timeSetListener, curHour, curMin, true);
        dialog.show();
    }

    public void submitRes(View view){
        long fromTime = fromCal.getTime().getTime();
        long unixFromTime = fromTime/1000;
        long toTime = toCal.getTime().getTime();
        long unixToTime = toTime/1000;
        TextView userIdTxtView = findViewById(R.id.UserIdTxtView);
        userIdTxtView.setText(userId);
        String purpose = ((EditText) findViewById(R.id.PurposeEditTxt)).getText().toString();

        TextView messageView = findViewById(R.id.ResTxtView);
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromTime", unixFromTime);
            jsonObject.put("toTime", unixToTime);
            jsonObject.put("userId", userId);
            jsonObject.put("purpose", purpose);
            jsonObject.put("roomId", oneRes.getId());
            String jsonString = jsonObject.toString();
            messageView.setText(jsonString);
            PostJsonOkHttpTask task = new PostJsonOkHttpTask();
            task.execute("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations", jsonString);
            Log.d("submitRes", jsonString);
        } catch (JSONException ex) {
            messageView.setText(ex.getMessage());
            Log.d("submitResCatch", ex.getMessage());
        }
    }

    private class PostJsonOkHttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String postData = strings[1];
            MediaType mediaType = MediaType.parse("application(json");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(postData, mediaType);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Acept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    //Toast.makeText(AddReservation.this, "Your reservation has succeeded", Toast.LENGTH_LONG).show();
                    finish();
                    return response.body().string();
                } else {
                    return url + "\n" + response.code() + " " + response.message();
                }
            } catch (IOException ex) {
                Log.d("JsonPOST", ex.toString());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            TextView messageView = findViewById(R.id.ResTxtView);
            messageView.setText(jsonString);
            Log.d("PostExecuteRes", jsonString);
        }

        @Override
        protected void onCancelled(String message) {
            super.onCancelled(message);
            TextView messageView = findViewById(R.id.ResTxtView);
            messageView.setText(message);
            Log.d("CancelledRes", message);
        }
    }
}
