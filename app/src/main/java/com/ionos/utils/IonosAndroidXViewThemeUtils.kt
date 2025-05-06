/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH..
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils

class IonosAndroidXViewThemeUtils(
    private val delegate: AndroidXViewThemeUtils,
) {

    fun colorPrimaryTextViewElement(textView: AppCompatTextView) {
        delegate.colorPrimaryTextViewElement(textView)
    }

    fun colorSwitchCompat(switchCompat: SwitchCompat) {
        delegate.colorSwitchCompat(switchCompat)
    }

    fun themeActionBar(context: Context, actionBar: ActionBar, title: String, backArrow: Drawable) {
        actionBar.setHomeAsUpIndicator(backArrow)
        actionBar.title = title
    }

    fun themeActionBarSubtitle(context: Context, actionBar: ActionBar) {
        // do nothing
    }

    fun themeNotificationCompatBuilder(context: Context, builder: NotificationCompat.Builder) {
        delegate.themeNotificationCompatBuilder(context, builder)
    }

    fun themeSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        delegate.themeSwipeRefreshLayout(swipeRefreshLayout)
    }

    fun themeToolbarSearchView(searchView: SearchView) {
        // do nothing
    }
}
