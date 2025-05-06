/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.scanbot.screens.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

internal abstract class BaseWindowInsetsActivity : AppCompatActivity() {

	private val contentView: View by lazy {
		window.decorView.findViewById(android.R.id.content) ?: window.decorView
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && savedInstanceState != null) {
			WindowCompat.setDecorFitsSystemWindows(window, false)
			enableEdgeToEdge()
		}
	}

	override fun onStart() {
		super.onStart()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
			ViewCompat.setOnApplyWindowInsetsListener(contentView) { _, windowInsets ->
				onApplyWindowInsets(windowInsets)
			}
		}
	}

	override fun onStop() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
			ViewCompat.setOnApplyWindowInsetsListener(contentView, null)
		}
		super.onStop()
	}

	protected abstract fun onApplyWindowInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat
}
