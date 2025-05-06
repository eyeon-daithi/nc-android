/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.scanbot.screens.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.addCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.ionos.scanbot.R
import com.ionos.scanbot.databinding.ScanbotActivityGalleryBinding
import com.ionos.scanbot.screens.base.BaseActivity
import com.ionos.scanbot.screens.common.ExitDialog
import com.ionos.scanbot.screens.common.LockProgressDialog
import com.ionos.scanbot.screens.common.use_case.open_screen.OpenScreen
import com.ionos.scanbot.screens.common.use_case.open_screen.OpenScreenIntent.*
import com.ionos.scanbot.screens.gallery.GalleryScreen.ColorFilterIcon
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event.DisplayPicturesEvent
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event.OpenScreenEvent
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event.PerformExitEvent
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event.ShowErrorMessageEvent
import com.ionos.scanbot.screens.gallery.GalleryScreen.Event.ShowExitDialogEvent
import com.ionos.scanbot.screens.gallery.GalleryScreen.PageInfo
import com.ionos.scanbot.screens.gallery.GalleryScreen.State
import com.ionos.scanbot.screens.gallery.GalleryScreen.ViewModel
import com.ionos.scanbot.screens.gallery.pager.GalleryPagerAdapter
import com.ionos.scanbot.util.widget.addOnPageSelectedListener
import com.ionos.scanbot.util.window.containsSideNavigationBar
import com.ionos.scanbot.util.window.getSystemBarsAndDisplayCutoutInsets
import javax.inject.Inject

internal class GalleryActivity : BaseActivity<Event, State, ViewModel>() {

	companion object {
		private const val BUNDLE_PICTURE_ID: String = "BUNDLE_PICTURE_ID"

		fun createIntent(context: Context, pictureId: String? = null): Intent {
			return Intent(context, GalleryActivity::class.java).apply {
				pictureId?.let { putExtra(BUNDLE_PICTURE_ID, it) }
				addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			}
		}
	}

	override val viewModelFactory by lazy { viewModelFactoryAssistant.create(getPictureId()) }
	override val viewBinding by lazy { ScanbotActivityGalleryBinding.inflate(layoutInflater) }

    @Inject lateinit var viewModelFactoryAssistant: GalleryViewModelFactory.Assistant
    @Inject lateinit var viewPagerAdapter: GalleryPagerAdapter
    @Inject lateinit var exitDialog: ExitDialog
	private val progressDialog by lazy { LockProgressDialog() }
	private val openScreen = OpenScreen(this)

	private fun getPictureId(): String? {
		return intent?.getStringExtra(BUNDLE_PICTURE_ID)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initOnBackPressedCallback()
		initListeners()
		viewBinding.vpPager.adapter = viewPagerAdapter
	}

	override fun onStart() {
		super.onStart()
		viewModel.onStart()
	}

    override fun onApplyWindowInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        windowWrapper.setupStatusBar(theme, R.attr.scanbot_app_bar_color, false)
        if (windowInsets.containsSideNavigationBar()) {
            windowWrapper.setupNavigationBar(theme, R.attr.scanbot_window_background, true)
        } else {
            windowWrapper.setupNavigationBar(theme, R.attr.scanbot_gallery_bottom_navigation_color, true)
        }

        val insets = windowInsets.getSystemBarsAndDisplayCutoutInsets()
        val defaultBottomPadding = resources.getDimension(R.dimen.scanbot_padding_small).toInt()
        val defaultFabMargin = resources.getDimension(R.dimen.scanbot_gallery_fab_margin).toInt()

        viewBinding.toolbar.toolbar.setPadding(insets.left, insets.top, insets.right, 0)
        viewBinding.llBottomNavigation.setPadding(insets.left, 0, insets.right, insets.bottom + defaultBottomPadding)
        viewBinding.fabScanbotAdd.updateLayoutParams<MarginLayoutParams> {
            rightMargin = defaultFabMargin + insets.right
        }

        return WindowInsetsCompat.CONSUMED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OpenSaveScreenIntent.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onSuccessSaveScreenResult()
        }
    }

	private fun initOnBackPressedCallback() {
		onBackPressedDispatcher.addCallback(this, true) { viewModel.onBackPressed() }
	}

	private fun initListeners() = with(viewBinding) {
		vpPager.addOnPageSelectedListener { viewModel.onPageSelected(it) }
		toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
		toolbar.tvSave.setOnClickListener { viewModel.onSaveButtonClicked() }
		fabScanbotAdd.setOnClickListener { viewModel.onAddButtonClicked() }
		niCrop.setOnClickListener { viewModel.onCropButtonClicked() }
		niFilter.setOnClickListener { viewModel.onFilterButtonClicked() }
		niRotate.setOnClickListener { viewModel.onRotateButtonClicked() }
		niRearrange.setOnClickListener { viewModel.onRearrangeButtonClicked() }
		niDelete.setOnClickListener { viewModel.onDeleteButtonClicked() }
	}

	override fun State.render() {
		renderTitle(title)
		renderPageInfo(pageInfo)
		renderFilterIcon(filterIcon)
		renderProgress(processing)
	}

	private fun renderTitle(title: String) = with(viewBinding) {
		toolbar.tvTitle.text = title
	}

	private fun renderPageInfo(pageInfo: PageInfo?) = with(viewBinding) {
		if (pageInfo != null) {
			val pageNumber = pageInfo.index + 1
			vpPager.setCurrentItem(pageInfo.index, false)
			tvCounter.text = getString(R.string.scanbot_gallery_counter_formatted, pageNumber, pageInfo.total)
			tvCounter.visibility = View.VISIBLE
		} else {
			tvCounter.visibility = View.INVISIBLE
		}
	}

	private fun renderFilterIcon(filterIcon: ColorFilterIcon) = with(viewBinding) {
		niFilter.setImageResource(filterIcon.imageRes)
		niFilter.setBackgroundResource(filterIcon.backgroundRes)
	}

	private fun renderProgress(display: Boolean) = if (display) {
		progressDialog.start(this, getString(R.string.scanbot_processing_title))
	} else {
		progressDialog.stop()
	}

	override fun Event.handle() = when (this) {
		is DisplayPicturesEvent -> viewPagerAdapter.setPictures(pictures)
		is OpenScreenEvent -> openScreen(intent)
		is PerformExitEvent -> finish()
		is ShowErrorMessageEvent -> showMessage(R.string.scanbot_fail)
		is ShowExitDialogEvent -> showExitDialog()
	}

	private fun showExitDialog() {
		exitDialog.show(context, viewModel::onExitConfirmed, viewModel::onExitDenied)
	}
}
