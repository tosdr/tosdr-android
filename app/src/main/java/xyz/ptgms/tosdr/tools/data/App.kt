package xyz.ptgms.tosdr.tools.data

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class App(
    val name: String,
    val icon: Drawable,
    val packageName: String,
)