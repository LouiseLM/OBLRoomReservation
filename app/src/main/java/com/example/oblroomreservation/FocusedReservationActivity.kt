package com.example.oblroomreservation

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FocusedReservationActivity : AppCompatActivity() {
    private var reservation: JsonReservation? = null
    private var gestureDetector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focused_reservation)
        val intent = intent
        reservation = intent.getSerializableExtra(FOCUSRES) as JsonReservation
        val DeleteBtn = findViewById<Button>(R.id.DeleteBtn)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                Log.d("gestDecURes", "onFling")
                val onSwipe = e1.x < e2.x
                if (onSwipe) {
                    Log.d("gestDecURes2", "onSwipe")
                    finish()
                }
                return true
            }
        })
    }

    fun deleteRes(view: View) {
        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/" + reservation!!.id
        val client = OkHttpClient()
        val request = Request.Builder().url(url).delete().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, ex: IOException) {
                runOnUiThread {
                    Log.d("delOnFail", "delOnFail")
                    val messageView = findViewById<TextView>(R.id.DelMsgTxtView)
                    messageView.text = ex.message
                }
            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {
                    Log.d("delOnResp", "delOnResp")
                    val messageView = findViewById<TextView>(R.id.DelMsgTxtView)
                    if (response.isSuccessful) {
                        messageView.text = "Reservation deleted"
                        Toast.makeText(this@FocusedReservationActivity, "Your reservation has been deleted", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        messageView.text = "Something went wrong! \n" + url + "\n" + response.code + " " + response.message
                    }
                }
            }
        })
    }

    companion object {
        var FOCUSRES = "FOCUSRES"
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }
}