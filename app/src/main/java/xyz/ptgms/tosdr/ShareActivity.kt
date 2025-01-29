package xyz.ptgms.tosdr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareActivity : Activity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        Log.i("ShareActivity", "Mime type: ${intent.type}")
        if (Intent.ACTION_SEND == appLinkAction && "text/plain" == intent.type) {
            handleSendText(intent)
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            val url = Uri.parse(it)
            val domain = url.host?.split(".")?.takeLast(2)?.joinToString(".")

            if (domain == null) {
                finish()
                return
            }

            val dialog = AlertDialog.Builder(this)

            dialog.setTitle(getString(R.string.share_question))
            dialog.setMessage(getString(R.string.share_question_desc, domain))
            dialog.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                searchAndOpenService(domain)
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

    private fun searchAndOpenService(domain: String) {
        coroutineScope.launch {
            try {
                val response = ApiClient.api.searchServices(domain)
                if (response.services.isNotEmpty()) {
                    val serviceId = response.services[0].id
                    val intent = Intent(this@ShareActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        data = Uri.parse("tosdr://service/$serviceId")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ShareActivity,
                        getString(R.string.share_no_results),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (_: Exception) {
                Toast.makeText(
                    this@ShareActivity,
                    getString(R.string.share_no_results),
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                finish()
            }
        }
    }
}