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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.content.Intent




class MainActivity : AppCompatActivity() {
    private lateinit var insertButton: Button
    private lateinit var queryButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var dbHelper: SQLiteOpenHelper
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: Button
    private lateinit var checkbox1: CheckBox
    private lateinit var checkbox2: CheckBox
    private lateinit var showFirstButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        inputEditText = findViewById(R.id.input_edit_text)
        insertButton = findViewById(R.id.insert_button)
        queryButton = findViewById(R.id.query_button)
        resultTextView = findViewById(R.id.result_text_view)
        clearButton = findViewById(R.id.clear_button)
        checkbox1 = findViewById(R.id.checkBox)
        checkbox2 = findViewById(R.id.checkBox2)
        showFirstButton = findViewById(R.id.show_first_button)
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

        clearButton.setOnClickListener {
            clearData()
            resultTextView.text = ""
        }
        val onCheckedChangeListener = { _: CheckBox, _: Boolean ->
            showFirstButton.isEnabled = checkbox1.isChecked || checkbox2.isChecked
        }

        checkbox1.setOnCheckedChangeListener { _, _ ->
            showFirstButton.isEnabled = checkbox1.isChecked || checkbox2.isChecked
        }

        checkbox2.setOnCheckedChangeListener { _, _ ->
            showFirstButton.isEnabled = checkbox1.isChecked || checkbox2.isChecked
        }

        showFirstButton.setOnClickListener {
            val firstEntry = readFirstData()
            val lastEntry = readLastData()
            val intent = Intent(this, DisplayDataActivity::class.java)
            when {
                checkbox1.isChecked && checkbox2.isChecked -> {
                    intent.putExtra("firstEntry", firstEntry?.title)
                    intent.putExtra("lastEntry", lastEntry?.title)
                }
                checkbox1.isChecked -> {
                    intent.putExtra("firstEntry", firstEntry?.title)
                }
                checkbox2.isChecked -> {
                    intent.putExtra("lastEntry", lastEntry?.title)
                }
            }

            startActivity(intent)
            when {
                checkbox1.isChecked && checkbox2.isChecked -> {
                    val result = mutableListOf<String>()
                    firstEntry?.let { result.add("${it.title}") }
                    lastEntry?.let { result.add("${it.title}") }
                    resultTextView.text = if (result.isEmpty()) "No data found" else result.joinToString("\n")
                }
                checkbox1.isChecked -> {
                    resultTextView.text = firstEntry?.let {
                        "${it.title}"
                    } ?: "No data found"
                }
                checkbox2.isChecked -> {
                    resultTextView.text = lastEntry?.let {
                        "${it.title}"
                    } ?: "No data found"
                }
                else -> {
                    resultTextView.text = ""
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun insertData(title: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE, title)
        }

        db.insert(SQLiteOpenHelper.DatabaseContract.Entry.TABLE_NAME, null, values)
    }

    private fun readData(): List<String> {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID,
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE
        )

        val cursor = db.query(
            SQLiteOpenHelper.DatabaseContract.Entry.TABLE_NAME,
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
                val itemTitle = getString(getColumnIndexOrThrow(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE))
                items.add(itemTitle)
            }
        }
        cursor.close()
        return items
    }

    private fun clearData() {
        val db = dbHelper.writableDatabase
        db.delete(SQLiteOpenHelper.DatabaseContract.Entry.TABLE_NAME, null, null)
    }

    private fun readFirstData(): SQLiteOpenHelper.Entry? {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID,
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE
        )

        val sortOrder = "${SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID} ASC"
        val cursor = db.query(
            SQLiteOpenHelper.DatabaseContract.Entry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder,
            "1"
        )

        var entry: SQLiteOpenHelper.Entry? = null
        with(cursor) {
            if (moveToFirst()) {
                val itemId = getLong(getColumnIndexOrThrow(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID))
                val itemTitle = getString(getColumnIndexOrThrow(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE))
                entry = SQLiteOpenHelper.Entry(itemId, itemTitle)
            }
        }
        cursor.close()
        return entry
    }

    private fun readLastData(): SQLiteOpenHelper.Entry? {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID,
            SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE
        )

        val sortOrder = "${SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID} DESC"
        val cursor = db.query(
            SQLiteOpenHelper.DatabaseContract.Entry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder,
            "1"
        )

        var entry: SQLiteOpenHelper.Entry? = null
        with(cursor) {
            if (moveToFirst()) {
                val itemId = getLong(getColumnIndexOrThrow(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_ID))
                val itemTitle = getString(getColumnIndexOrThrow(SQLiteOpenHelper.DatabaseContract.Entry.COLUMN_NAME_TITLE))
                entry = SQLiteOpenHelper.Entry(itemId, itemTitle)
            }
        }
        cursor.close()
        return entry
    }
}