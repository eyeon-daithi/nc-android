/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH..
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.startup

import android.content.Context
import androidx.startup.Initializer
import com.ionos.analycis.AnalyticsManager
import com.ionos.privacy.PrivacyPreferences
import com.ionos.scanbot.initializer.ScanbotInitializer
import com.nextcloud.client.preferences.AppPreferences
import com.owncloud.android.MainApp
import javax.inject.Inject

class IonosInitializer : Initializer<Unit> {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var privacyPreferences: PrivacyPreferences

    @Inject
    lateinit var scanbotInitializer: ScanbotInitializer

    override fun create(context: Context) {
        (context.applicationContext as MainApp).androidInjector().inject(this)
        analyticsManager.setEnabled(privacyPreferences.isAnalyticsEnabled())
        appPreferences.setShowHiddenFilesEnabled(true);
        scanbotInitializer.initialize()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
