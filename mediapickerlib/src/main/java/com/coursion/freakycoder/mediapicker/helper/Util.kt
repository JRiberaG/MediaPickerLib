package com.coursion.freakycoder.mediapicker.helper

import android.content.res.ColorStateList
import android.os.Build
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.ViewCompat

/**
 * Created by WrathChaos on 5.03.2018.
 */
class Util{
    companion object {
        fun setButtonTint(button: FloatingActionButton, tint: ColorStateList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.backgroundTintList = tint
            } else {
                ViewCompat.setBackgroundTintList(button, tint)
            }
        }
    }
}