package com.example.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val dest = findViewById<EditText>(R.id.dest)
        val keyword = findViewById<EditText>(R.id.keyword)
        val enabled = findViewById<Switch>(R.id.enabled)

        dest.setText(prefs.getString("dest", ""))
        keyword.setText(prefs.getString("keyword", "SBM"))
        enabled.isChecked = prefs.getBoolean("enabled", false)

        enabled.setOnCheckedChangeListener { _, on ->
            if (on && dest.text.isBlank()) {
                enabled.isChecked = false
                Toast.makeText(this, "Enter a destination number first", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }
            if (on) requestPerms()
            prefs.edit()
                .putString("dest", dest.text.toString().trim())
                .putString("keyword", keyword.text.toString().trim())
                .putBoolean("enabled", on)
                .apply()
        }
        findViewById<android.widget.Button>(R.id.save).setOnClickListener {
            prefs.edit()
                .putString("dest", dest.text.toString().trim())
                .putString("keyword", keyword.text.toString().trim())
                .apply()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPerms() {
        val need = listOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)
            .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (need.isNotEmpty()) requestPermissions(need.toTypedArray(), 1)
    }
}
