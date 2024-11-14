package com.example.todo_p

import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo_p.databinding.ActivityAlarmPageBinding

class AlarmPage : AppCompatActivity() {

    lateinit var binding: ActivityAlarmPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlarmPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button.setOnClickListener {
            val taskId = intent.getLongExtra("taskId", -1)

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


                val intent = Intent(this, AlarmManagerBroadcast::class.java).apply {
                    putExtra("taskId", taskId)
                }

                val taskA = TaskActivity()
                val pendingIntent = taskA.getpendingIntent()

                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show()
        }



    }
}