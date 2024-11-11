package com.example.todo_p

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel (
    var title: String,
    var description: String,
    var Category: String,
    var date: Long,
    var time: Long,
    var isFinished : Int = -1,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
 )
