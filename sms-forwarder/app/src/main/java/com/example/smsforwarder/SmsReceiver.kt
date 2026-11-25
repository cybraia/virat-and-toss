package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val prefs = context.getSharedPreferences("cfg", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enabled", false)) return
        val dest = prefs.getString("dest", "").orEmpty().trim()
        val keyword = prefs.getString("keyword", "SBM").orEmpty().trim()
        if (dest.isEmpty() || keyword.isEmpty()) return

        // Long messages arrive as several parts; join parts per sender.
        val parts = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val bySender = LinkedHashMap<String, StringBuilder>()
        for (p in parts) {
            bySender.getOrPut(p.originatingAddress ?: "unknown") { StringBuilder() }
                .append(p.messageBody ?: "")
        }

        for ((sender, body) in bySender) {
            val text = body.toString()
            // Case-insensitive match; skip messages from the destination (avoids loops).
            if (!text.contains(keyword, ignoreCase = true)) continue
            if (sender.filter { it.isDigit() }.takeLast(10) ==
                dest.filter { it.isDigit() }.takeLast(10)) continue
            forward(context, dest, "From $sender:\n$text")
        }
    }

    private fun forward(context: Context, dest: String, msg: String) {
        val sms = if (Build.VERSION.SDK_INT >= 31)
            context.getSystemService(SmsManager::class.java)
        else @Suppress("DEPRECATION") SmsManager.getDefault()
        val chunks = sms.divideMessage(msg)
        sms.sendMultipartTextMessage(dest, null, chunks, null, null)
    }
}
