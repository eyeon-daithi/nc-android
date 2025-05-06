/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.scanbot.util.window

import android.content.res.Resources.Theme
import android.os.Build
import android.util.TypedValue
import android.view.Window
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat

class WindowWrapper(
    private val window: Window,
) {
    private val insetsController = WindowCompat.getInsetsController(window, window.decorView)

    fun setupStatusBar(theme: Theme, @AttrRes backgroundColorAttr: Int, contrastEnforced: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.window.setStatusBarContrastEnforced(contrastEnforced)
        }
        theme.resolveColorAttr(backgroundColorAttr) {
            this.insetsController.isAppearanceLightStatusBars = isLightColor(it)
        }
    }

    fun setupNavigationBar(theme: Theme, @AttrRes backgroundColorAttr: Int, contrastEnforced: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.window.setNavigationBarContrastEnforced(contrastEnforced)
        }
        theme.resolveColorAttr(backgroundColorAttr) {
            this.window.navigationBarColor = it
            this.insetsController.isAppearanceLightNavigationBars = isLightColor(it)
        }
    }

    private fun Theme.resolveColorAttr(@AttrRes colorAttr: Int, action: (color: Int) -> Unit) {
        val outValue = TypedValue()
        if (resolveAttribute(colorAttr, outValue, true)) {
            action.invoke(outValue.data)
        }
    }

    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) > 0.5
    }
}
