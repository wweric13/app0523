package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent


class DisplayDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_data)

        val firstEntryTitle = intent.getStringExtra("firstEntry")
        val lastEntryTitle = intent.getStringExtra("lastEntry")

        val textView = findViewById<TextView>(R.id.display_text_view)

        if (firstEntryTitle != null && lastEntryTitle != null) {
            textView.text = " $firstEntryTitle\nLast Entry: $lastEntryTitle"
        } else if (firstEntryTitle != null) {
            textView.text = " $firstEntryTitle"
        } else if (lastEntryTitle != null) {
            textView.text = " $lastEntryTitle"
        }
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}