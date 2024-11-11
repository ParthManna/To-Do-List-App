package com.example.todo_p

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todo_p.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Partha Sarathi Manna

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy{
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        binding.todoRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        initSwipe()

        db.todoDao().getTask().observe(this, Observer { tasks ->
            list.clear()
            if (!tasks.isNullOrEmpty()) {
                list.addAll(tasks)
            }
            adapter.notifyDataSetChanged() // Update adapter only when database content changes
        })



        binding.fab.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
        }
    }

    fun initSwipe(){
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                val itemId = adapter.getItemId(position)

                lifecycleScope.launch(Dispatchers.IO) {
                    if (direction == ItemTouchHelper.LEFT) {
                        db.todoDao().deleteTask(itemId)
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        db.todoDao().finishTask(itemId)
                    }

                    withContext(Dispatchers.Main) {
                        // Remove the item from the list and notify the adapter about the removal
                        list.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                }
            }




            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon:Bitmap

                    if(dX > 0){

                        icon = BitmapFactory.decodeResource(resources, R.drawable.checkmark_32)

                        paint.color = Color.parseColor("#00ff95")

                        canvas.drawRect(
                            itemView.left.toFloat(),itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(),paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2,
                            paint
                        )
                    }else{
                        icon = BitmapFactory.decodeResource(resources, R.drawable.delete_32)

                        paint.color = Color.parseColor("#FF0000")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX,itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(),paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2,
                            paint
                        )
                    }

                    viewHolder.itemView.translationX = dX

                }
                else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.todoRv)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history ->{
                startActivity(Intent(this, HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}