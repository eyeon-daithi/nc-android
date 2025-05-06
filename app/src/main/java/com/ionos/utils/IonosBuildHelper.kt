/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.utils

import com.owncloud.android.BuildConfig

object IonosBuildHelper {

    private const val IONOS_APPLICATION_ID = "com.ionos.hidrivenext"

    @JvmStatic
    fun isIonosBuild(): Boolean {
        return IONOS_APPLICATION_ID == BuildConfig.APPLICATION_ID
    }
}
