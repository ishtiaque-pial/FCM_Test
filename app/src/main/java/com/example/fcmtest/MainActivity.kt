package com.example.fcmtest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txt =findViewById<TextView>(R.id.qwe)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->


            // Get new FCM registration token
            val token = task.result

            // Log and toast
            txt.text= token
            //Log.d("nsbcmbsmcnxmcb", token)
        }
        txt.setOnClickListener {
            copyToClipboard(txt.text.toString())
            Toast.makeText(this,"Copied",Toast.LENGTH_LONG).show()
        }
    }
}

fun Context.copyToClipboard(text: CharSequence){
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("",text))
}