/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils
import com.nextcloud.android.common.ui.theme.utils.ColorRole
import com.nextcloud.android.common.ui.util.buildColorStateList
import com.owncloud.android.R

class IonosAndroidViewThemeUtils(
    private val delegate: AndroidViewThemeUtils,
) {
    fun themeStatusBar(activity: Activity) {
        delegate.colorStatusBar(activity, activity.getSystemBarsColor())
    }

    fun themeStatusBar(activity: Activity, @ColorInt color: Int) {
        delegate.colorStatusBar(activity, color)
    }

    fun resetStatusBar(activity: Activity) {
        delegate.colorStatusBar(activity, activity.getSystemBarsColor())
    }

    @ColorInt
    private fun Context.getSystemBarsColor(): Int = getColor(R.color.system_bars_color)

    @JvmOverloads
    fun colorViewBackground(view: View, colorRole: ColorRole = ColorRole.SURFACE) {
        if (colorRole == ColorRole.SURFACE) {
            // do nothing
        } else {
            delegate.colorViewBackground(view, colorRole)
        }
    }

    fun themeDialog(view: View) {
        // do nothing
    }

    fun colorTextButtons(vararg buttons: Button) {
        // do nothing
    }

    fun colorCircularProgressBar(progressBar: ProgressBar, colorRole: ColorRole) {
        delegate.colorCircularProgressBar(progressBar, colorRole)
    }

    fun colorDrawable(drawable: Drawable, @ColorInt color: Int): Drawable {
        return delegate.colorDrawable(drawable, color)
    }

    fun colorEditText(editText: EditText) {
        delegate.colorEditText(editText)
    }

    fun colorEditTextOnPrimary(editText: EditText) {
        delegate.colorEditTextOnPrimary(editText)
    }

    fun colorImageView(imageView: ImageView) {
        delegate.colorImageView(imageView)
    }

    fun colorImageView(imageView: ImageView, colorRole: ColorRole) {
        delegate.colorImageView(imageView, colorRole)
    }

    fun colorImageViewBackgroundAndIcon(imageView: ImageView) {
        delegate.colorImageViewBackgroundAndIcon(imageView)
    }

    fun colorMenuItemText(context: Context, item: MenuItem) {
        delegate.colorMenuItemText(context, item)
    }

    fun colorOnSecondaryContainerTextViewElement(textView: TextView) {
        delegate.colorOnSecondaryContainerTextViewElement(textView)
    }

    fun colorPrimaryTextViewElement(textView: TextView) {
        delegate.colorPrimaryTextViewElement(textView)
    }

    fun colorStatusBar(activity: Activity, @ColorInt color: Int) {
        delegate.colorStatusBar(activity, color)
    }

    fun colorSwitch(switch: Switch) {
        delegate.colorSwitch(switch)
    }

    fun colorTextButtons(@ColorInt color: Int, vararg buttons: Button) {
        delegate.colorTextButtons(color, *buttons)
    }

    @JvmOverloads
    fun colorTextView(textView: TextView, colorRole: ColorRole = ColorRole.PRIMARY) {
        delegate.colorTextView(textView, colorRole)
    }

    fun colorToolbarMenuIcon(context: Context, item: MenuItem) {
        delegate.colorToolbarMenuIcon(context, item)
    }

    fun getPrimaryColorDrawable(context: Context): Drawable {
        return delegate.getPrimaryColorDrawable(context)
    }

    fun primaryColor(activity: Activity): Int {
        return delegate.primaryColor(activity)
    }

    fun themeCheckbox(vararg checkboxes: CheckBox) {
        delegate.themeCheckbox(*checkboxes)
    }

    fun themeCheckedTextView(vararg checkedTextViews: CheckedTextView) {
        delegate.themeCheckedTextView(*checkedTextViews)
    }

    fun themeDialogDivider(view: View) {
        delegate.themeDialogDivider(view)
    }

    fun themeHorizontalProgressBar(progressBar: ProgressBar) {
        delegate.themeHorizontalProgressBar(progressBar)
    }

    fun themeRadioButton(radioButton: RadioButton) {
        delegate.themeRadioButton(radioButton)
    }

    fun themeStatusBar(activity: Activity, colorRole: ColorRole) {
        delegate.themeStatusBar(activity, colorRole)
    }

    @JvmOverloads
    fun tintDrawable(context: Context, drawable: Drawable, colorRole: ColorRole = ColorRole.PRIMARY): Drawable {
        return delegate.tintDrawable(context, drawable, colorRole)
    }

    @JvmOverloads
    fun tintDrawable(context: Context, @DrawableRes id: Int, colorRole: ColorRole = ColorRole.PRIMARY): Drawable? {
        return delegate.tintDrawable(context, id, colorRole)
    }

    fun tintPrimaryDrawable(context: Context, drawable: Drawable?): Drawable? {
        return delegate.tintPrimaryDrawable(context, drawable)
    }

    fun tintPrimaryDrawable(context: Context, @DrawableRes id: Int): Drawable? {
        return delegate.tintPrimaryDrawable(context, id)
    }

    fun tintTextDrawable(context: Context, drawable: Drawable?): Drawable? {
        return delegate.tintTextDrawable(context, drawable)
    }

    fun colorBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        with(bottomNavigationView) {
            setBackgroundResource(R.color.ionos_bottom_navigation_background_color)

            val iconColorInt = context.getColor(R.color.ionos_bottom_navigation_icon_color)
            itemIconTintList = ColorStateList.valueOf(iconColorInt)

            val activeIndicatorColorInt = context.getColor(R.color.ionos_bottom_navigation_active_indicator_color)
            itemActiveIndicatorColor = ColorStateList.valueOf(activeIndicatorColorInt)

            val rippleColorInt = context.getColor(R.color.ionos_bottom_navigation_ripple_color)
            itemRippleColor = ColorStateList.valueOf(rippleColorInt)

            itemTextColor = buildColorStateList(
                android.R.attr.state_checked to context.getColor(R.color.text_color),
                -android.R.attr.state_checked to context.getColor(R.color.secondary_text_color),
            )
        }
    }
}
