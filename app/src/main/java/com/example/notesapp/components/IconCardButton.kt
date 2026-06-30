package com.example.notesapp.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.example.notesapp.R
import com.example.notesapp.databinding.ViewIconCardButtonBinding

class IconCardButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewIconCardButtonBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {

        context.withStyledAttributes(
            attrs,
            R.styleable.IconCardButton
        ) {

            val icon = getResourceId(
                R.styleable.IconCardButton_iconSrc,
                0
            )

            if (icon != 0) {
                binding.ivIcon.setImageResource(icon)
            }
        }
    }

    fun setIcon(iconRes: Int) {
        binding.ivIcon.setImageResource(iconRes)
    }
}