package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object CommunicationActions {

    fun openDialer(context: Context, phone: String) {
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        if (cleanPhone.isBlank()) {
            Toast.makeText(context, "رقم الهاتف غير متوفر", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanPhone")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق الاتصال", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, phone: String, message: String = "السلام عليكم، بخصوص عرضكم على تطبيق تخفيضات أجدابيا:") {
        var cleanPhone = phone.replace(Regex("[^0-9]"), "")
        if (cleanPhone.startsWith("09")) {
            // Libya international prefix 218
            cleanPhone = "218" + cleanPhone.substring(1)
        } else if (!cleanPhone.startsWith("218") && cleanPhone.length == 9) {
            cleanPhone = "218$cleanPhone"
        }
        
        try {
            val uri = Uri.parse("https://wa.me/$cleanPhone?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق واتساب", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMessenger(context: Context, messengerUrlOrPage: String) {
        try {
            val targetUrl = when {
                messengerUrlOrPage.startsWith("http") -> messengerUrlOrPage
                messengerUrlOrPage.isNotBlank() -> "https://m.me/$messengerUrlOrPage"
                else -> "https://m.me"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح ماسنجر", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFacebookPost(context: Context, url: String) {
        try {
            val targetUrl = if (url.startsWith("http")) url else "https://$url"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح رابط فيسبوك", Toast.LENGTH_SHORT).show()
        }
    }
}
