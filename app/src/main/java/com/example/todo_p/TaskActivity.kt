package com.example.todo_p

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import java.util.Calendar
import com.example.todo_p.databinding.ActivityTaskBinding
import java.text.SimpleDateFormat

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTaskBinding

    lateinit var myCalender: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

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

        binding.dateEdt.setOnClickListener{ v : View ->
            when(v.id){
                R.id.dateEdt -> {
                    setListener()
                }
            }
        }

    }

    private fun setListener() {
        myCalender = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this, dateSetListener, myCalender.get(Calendar.YEAR),
            myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myformat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myformat)
        binding.dateEdt.setText(sdf.format(myCalender.time))

        binding.timeInptLay.visibility = View.VISIBLE
    }

}