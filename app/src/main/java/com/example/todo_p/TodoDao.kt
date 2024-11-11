package com.example.todo_p

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {

    @Insert
    suspend fun insetTask(todoModel: TodoModel): Long

    @Query("Select * from TodoModel where isFinished == -1")
    fun getTask(): LiveData<List<TodoModel>>

    @Query("Update TodoModel set isFinished = 1 where id=:uid")
    suspend fun finishTask(uid: Long)

    @Query("Delete from TodoModel where id=:uid")
    suspend fun deleteTask(uid: Long)
}