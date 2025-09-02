/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2023 Álvaro Brey <alvaro@alvarobrey.com>
 * SPDX-FileCopyrightText: 2023 Nextcloud GmbH
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.nextcloud.client.di

import androidx.activity.result.contract.ActivityResultContract
import com.ionos.scanbot.availability.Availability
import com.ionos.scanbot.di.qualifiers.Scanbot
import com.ionos.scanbot.di.qualifiers.ScanbotLicense
import com.nextcloud.client.documentscan.AppScanOptionalFeature
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal class VariantModule {
    @Provides
    @Reusable
    fun scanOptionalFeature(
        @Scanbot featureAvailability: Availability,
        @ScanbotLicense licenseAvailability: Availability
    ): AppScanOptionalFeature {
        return object : AppScanOptionalFeature() {
            override fun getScanContract(): ActivityResultContract<Unit, String?> {
                throw UnsupportedOperationException("Document scan is not available")
            }
            override val isAvailable: Boolean = featureAvailability.available() && licenseAvailability.available()
        }
    }
}
