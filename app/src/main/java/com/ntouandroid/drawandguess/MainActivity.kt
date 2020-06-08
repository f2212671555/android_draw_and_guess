package com.ntouandroid.drawandguess

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ntouandroid.drawandguess.filter.NameInputFilter

class MainActivity : AppCompatActivity() {

    lateinit var bt_Test:Button
    lateinit var Bt_Start: Button;
    lateinit var Et_Name: EditText;
    lateinit var app_context: Context;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Bt_Start = findViewById(R.id.button_start);
        Et_Name = findViewById(R.id.Et_Name)
        Et_Name.filters = arrayOf(NameInputFilter())
        Bt_Start.setOnClickListener{nextpagecheck()};
        app_context = applicationContext;
    }


    fun nextpagecheck(){
        val name = Et_Name.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(this,"請入名字",Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this@MainActivity, StartActivity::class.java)
        intent.putExtra("userName",name )
        startActivity(intent);
    }

    fun btn_test_click(){
        var intent = Intent(this@MainActivity,MyTestActivity::class.java)
        startActivity(intent)
    }

}
