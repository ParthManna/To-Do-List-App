package com.example.todo_p

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import java.util.Calendar
import com.example.todo_p.databinding.ActivityTaskBinding

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTaskBinding

    lateinit var myCalender: Calendar

    lateinit var datasetListener:DatePickerDialog.OnDateSetListener

    val db by lazy{
        Room.databaseBuilder(this,
            AppDatabase::class.java,
            DB_NAME)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}