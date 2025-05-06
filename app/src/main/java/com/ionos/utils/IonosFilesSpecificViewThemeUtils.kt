/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2022 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2022 Nextcloud GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later OR GPL-2.0-only
 */
package com.ionos.utils

import android.content.Context
import android.preference.PreferenceCategory
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import com.google.android.material.card.MaterialCardView
import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase
import com.owncloud.android.R
import com.owncloud.android.lib.resources.shares.ShareType
import com.owncloud.android.utils.theme.FilesSpecificViewThemeUtils
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import javax.inject.Inject

class IonosFilesSpecificViewThemeUtils @Inject constructor(
    schemes: MaterialSchemes,
    private val delegate: FilesSpecificViewThemeUtils,
) : ViewThemeUtilsBase(schemes) {

    fun themePreferenceCategory(category: PreferenceCategory) {
        // Do nothing
    }

    @JvmOverloads
    fun themeActionBar(context: Context, actionBar: ActionBar, title: String, isMenu: Boolean = false) {
        val icon = getHomeAsUpIcon(isMenu)
        actionBar.setHomeAsUpIndicator(icon)
        actionBar.title = title
    }

    @JvmOverloads
    fun themeActionBar(context: Context, actionBar: ActionBar, isMenu: Boolean = false) {
        val icon = getHomeAsUpIcon(isMenu)
        actionBar.setHomeAsUpIndicator(icon)
    }

    @JvmOverloads
    fun themeActionBar(context: Context, actionBar: ActionBar, @StringRes titleRes: Int, isMenu: Boolean = false) {
        val title = context.getString(titleRes)
        themeActionBar(context, actionBar, title, isMenu)
    }

    private fun getHomeAsUpIcon(isMenu: Boolean): Int {
        val icon = if (isMenu) {
            R.drawable.ic_menu
        } else {
            R.drawable.ic_arrow_back
        }
        return icon
    }

    fun createAvatar(type: ShareType?, avatar: ImageView, context: Context) {
        delegate.createAvatar(type, avatar, context)
    }

    fun themeFastScrollerBuilder(context: Context, builder: FastScrollerBuilder): FastScrollerBuilder {
        return delegate.themeFastScrollerBuilder(context, builder)
    }

    fun themeTemplateCardView(cardView: MaterialCardView) {
        delegate.themeTemplateCardView(cardView)
    }

    fun themeStatusCardView(cardView: MaterialCardView) {
        delegate.themeStatusCardView(cardView)
    }

    fun themeAvatarButton(shareImageView: ImageView) {
        delegate.themeAvatarButton(shareImageView)
    }

    fun primaryColorToHexString(context: Context): String {
        return delegate.primaryColorToHexString(context)
    }

    fun setWhiteBackButton(context: Context, supportActionBar: ActionBar) {
        delegate.setWhiteBackButton(context, supportActionBar)
    }
}
