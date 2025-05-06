/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.scanbot.util.window

import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

fun WindowInsetsCompat.containsSideNavigationBar(): Boolean {
	val navigationBarInsets = getInsets(WindowInsetsCompat.Type.navigationBars())
	return max(navigationBarInsets.left, navigationBarInsets.right) > 0
}

fun WindowInsetsCompat.getSystemBarsAndDisplayCutoutInsets(): Insets {
	return getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
}
