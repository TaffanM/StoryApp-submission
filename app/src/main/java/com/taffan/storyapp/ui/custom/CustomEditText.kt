package com.taffan.storyapp.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText


class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }


    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (text.toString().length < 8) {
            setError("Password must be at least 8 characters", null)
        } else {
            error = null
        }
    }

    fun checkEditTextEmpty(): Boolean {
        return if (text.isNullOrEmpty()) {
            setError("You must fill this field", null)
            true
        } else {
            error = null
            false
        }
    }


}
