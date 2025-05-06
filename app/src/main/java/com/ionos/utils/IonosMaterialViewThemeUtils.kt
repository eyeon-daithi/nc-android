/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.utils

import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.nextcloud.android.common.ui.theme.utils.ColorRole
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils
import com.nextcloud.android.common.ui.util.buildColorStateList
import com.owncloud.android.R

class IonosMaterialViewThemeUtils(
    private val delegate: MaterialViewThemeUtils,
) {

    fun colorMaterialButtonPrimaryTonal(button: MaterialButton) {
        colorMaterialButtonPrimaryFilled(button)
    }

    fun colorMaterialButtonPrimaryBorderless(button: MaterialButton) {
        colorMaterialButtonPrimaryOutlined(button)
    }

    fun colorMaterialButtonPrimaryFilled(button: MaterialButton) {
        button.backgroundTintList = ContextCompat.getColorStateList(button.context, R.color.filled_button_bg_color)
        button.iconTint = ContextCompat.getColorStateList(button.context, R.color.filled_button_text_color)
        button.setTextColor(ContextCompat.getColorStateList(button.context, R.color.filled_button_text_color))
        val textPadding = button.resources.getDimension(R.dimen.button_text_padding).toInt()
        button.updatePadding(left = textPadding, right = textPadding)
    }

    fun colorMaterialButtonPrimaryOutlined(button: MaterialButton) {
        button.backgroundTintList = ContextCompat.getColorStateList(button.context, R.color.outlined_button_bg_color)
        button.iconTint = ContextCompat.getColorStateList(button.context, R.color.outlined_button_text_color)
        button.strokeColor = ContextCompat.getColorStateList(button.context, R.color.outlined_button_stroke_color)
        button.strokeWidth = button.resources.getDimension(R.dimen.outlined_button_stroke_width).toInt()
        button.setTextColor(ContextCompat.getColorStateList(button.context, R.color.outlined_button_text_color))
        val textPadding = button.resources.getDimension(R.dimen.button_text_padding).toInt()
        button.updatePadding(left = textPadding, right = textPadding)
    }

    fun colorMaterialButtonText(button: MaterialButton) {
        button.setTextColor(ContextCompat.getColorStateList(button.context, R.color.outlined_button_text_color))
    }

    fun themeSnackbar(snackbar: Snackbar) {}

    fun colorTextInputLayout(textInputLayout: TextInputLayout) {
        val context = textInputLayout.context

        val boxStrokeWidthRes = R.dimen.text_input_box_stroke_width
        val cornerRadiusRes = R.dimen.text_input_box_corner_radius
        val boxStrokeColorStateList = buildColorStateList(
            -android.R.attr.state_focused to context.getColor(R.color.text_input_border_stroke_color),
            android.R.attr.state_focused to context.getColor(R.color.text_input_focused_border_stroke_color),
        )

        textInputLayout.setBoxStrokeWidthResource(boxStrokeWidthRes)
        textInputLayout.setBoxStrokeWidthFocusedResource(boxStrokeWidthRes)
        textInputLayout.setBoxCornerRadiiResources(cornerRadiusRes, cornerRadiusRes, cornerRadiusRes, cornerRadiusRes)
        textInputLayout.setBoxStrokeColorStateList(boxStrokeColorStateList)

        val errorColorStateList = buildColorStateList(
            -android.R.attr.state_focused to context.getColor(R.color.text_input_error_color),
            android.R.attr.state_focused to context.getColor(R.color.text_input_error_color),
        )

        textInputLayout.setErrorIconTintList(errorColorStateList)
        textInputLayout.setErrorTextColor(errorColorStateList)
        textInputLayout.boxStrokeErrorColor = errorColorStateList

        val hintTextColorStateList = buildColorStateList(
            -android.R.attr.state_focused to context.getColor(R.color.text_input_hint_text_color),
            android.R.attr.state_focused to context.getColor(R.color.text_input_focused_hint_text_color),
        )

        textInputLayout.defaultHintTextColor = hintTextColorStateList

        val padding = context.resources.getDimension(R.dimen.text_input_padding).toInt()
        val textSize = context.resources.getDimension(R.dimen.text_input_text_size)

        textInputLayout.editText?.setPadding(padding)
        textInputLayout.editText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textInputLayout.editText?.highlightColor = context.getColor(R.color.text_input_highlight_color)
    }

    fun themeFAB(fab: FloatingActionButton) {
        delegate.themeFAB(fab)
    }

    fun colorMaterialButtonFilledOnPrimary(btn: MaterialButton) {
        delegate.colorMaterialButtonFilledOnPrimary(btn)
    }

    fun colorMaterialButtonOutlinedOnPrimary(btn: MaterialButton) {
        delegate.colorMaterialButtonOutlinedOnPrimary(btn)
    }

    fun colorMaterialTextButton(btn: MaterialButton) {
        delegate.colorMaterialTextButton(btn)
    }

    fun colorProgressBar(progressBar: LinearProgressIndicator) {
        delegate.colorProgressBar(progressBar)
    }

    fun themeChipSuggestion(chip: Chip) {
        delegate.themeChipSuggestion(chip)
    }

    fun colorTextInputLayout(til: TextInputLayout, colorRole: ColorRole) {
        delegate.colorTextInputLayout(til, colorRole)
    }

    fun themeTabLayout(tabLayout: TabLayout) {
        delegate.themeTabLayout(tabLayout)
    }

    fun colorMaterialSwitch(materialSwitch: MaterialSwitch) {
        delegate.colorMaterialSwitch(materialSwitch)
    }
}
