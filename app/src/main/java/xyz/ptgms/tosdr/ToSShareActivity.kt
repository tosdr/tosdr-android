package xyz.ptgms.tosdr

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import xyz.ptgms.tosdr.tools.data.api.API.searchPage
import kotlin.concurrent.thread

class ToSShareActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        intent.data
        Log.i("ToSShareActivity", "Mime type: ${intent.type}")
        if (Intent.ACTION_SEND == appLinkAction && "text/plain" == intent.type) {
            handleSendText(intent)
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Parse it into URL object and get the domain without subdomains.
            val url = Uri.parse(it)
            val domain = url.host?.split(".")?.takeLast(2)?.joinToString(".")

            if (domain == null) {
                finish()
                return
            }

            val dialog = AlertDialog.Builder(this)

            dialog.setTitle(getString(R.string.dialog_open_question))
            dialog.setMessage(getString(R.string.dialog_open_website).format(domain))
            dialog.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                // Get shared preferences
                val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
                thread {
                    val searchResult = searchPage(domain, prefs.getBoolean("hideGrade", false), prefs.getBoolean("hideNotReviewed", false))

                    // Set the report in the UI thread
                    runOnUiThread {
                        if (searchResult.isNotEmpty()) {
                            val id = searchResult[0].page
                            val grade = searchResult[0].grade
                            val deepLinkIntent = Intent(
                                Intent.ACTION_VIEW,
                                "tosdr://xyz.ptgms.space/$id/$grade".toUri(),
                                this,
                                MainActivity::class.java
                            )

                            val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                                addNextIntentWithParentStack(deepLinkIntent)
                                getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
                            }

                            deepLinkPendingIntent?.send()
                            finish()
                        } else {
                            Toast.makeText(this, getString(R.string.dialog_open_noresults), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }

            dialog.setNegativeButton(getString(android.R.string.cancel)) { _, _ ->
                finish()
            }

            dialog.setOnDismissListener {
                finish()
            }

            dialog.show()
        }
    }

}