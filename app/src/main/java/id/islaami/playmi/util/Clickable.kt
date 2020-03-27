package id.islaami.playmi.util

import android.text.style.ClickableSpan
import android.view.View

class Clickable(var position: Int, var action: (Int) -> Unit) : ClickableSpan() {
    override fun onClick(widget: View) {
        action(position)
    }
}