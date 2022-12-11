package xyz.ptgms.tosdr

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.billingclient.BuildConfig
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class Application : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        Log.i("ACRA Init", "Initialised!")

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            dialog {
                text = getString(R.string.crash_text)
                title = getString(R.string.crash_title)
                commentPrompt = getString(R.string.crash_comment)
                resTheme = android.R.style.Theme_Material_Dialog
                resIcon = android.R.drawable.stat_sys_warning
            }

            mailSender {
                mailTo = "me@ptgms.space"
                reportFileName = "Crash.txt"
            }
        }
    }
}