package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var insertButton: Button
    private lateinit var queryButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var dbHelper: SQLiteOpenHelper
    private lateinit var inputEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        inputEditText = findViewById(R.id.input_edit_text)
        insertButton = findViewById(R.id.insert_button)
        queryButton = findViewById(R.id.query_button)
        resultTextView = findViewById(R.id.result_text_view)
        dbHelper = SQLiteOpenHelper(this)
        
        insertButton.setOnClickListener {
            val title = inputEditText.text.toString()
            if (title.isNotBlank()) {
                insertData(title)
                inputEditText.text.clear()
            }
        }

        queryButton.setOnClickListener {
            val titles = readData()
            resultTextView.text = titles.joinToString("\n")
        }

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun insertData(title: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseContract.Entry.COLUMN_NAME_TITLE, title)
        }

        db.insert(DatabaseContract.Entry.TABLE_NAME, null, values)
    }

    private fun readData(): List<String> {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DatabaseContract.Entry.COLUMN_NAME_ID,
            DatabaseContract.Entry.COLUMN_NAME_TITLE
        )

        val cursor = db.query(
            DatabaseContract.Entry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val items = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val itemTitle = getString(getColumnIndexOrThrow(DatabaseContract.Entry.COLUMN_NAME_TITLE))
                items.add(itemTitle)
            }
        }
        cursor.close()
        return items
    }

}