/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.ionos.scanbot.screens.rearrange

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.addCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ItemTouchHelper
import com.ionos.scanbot.R
import com.ionos.scanbot.databinding.ScanbotActivityRearrangeBinding
import com.ionos.scanbot.screens.base.BaseActivity
import com.ionos.scanbot.screens.rearrange.RearrangeScreen.*
import com.ionos.scanbot.screens.rearrange.RearrangeScreen.Event.*
import com.ionos.scanbot.screens.rearrange.recycler.RearrangeAdapter
import com.ionos.scanbot.screens.rearrange.recycler.RearrangeCallback
import com.ionos.scanbot.util.window.getSystemBarsAndDisplayCutoutInsets
import javax.inject.Inject

internal class RearrangeActivity : BaseActivity<Event, State, ViewModel>() {
    @Inject override lateinit var viewModelFactory: RearrangeViewModelFactory
	override val viewBinding by lazy { ScanbotActivityRearrangeBinding.inflate(layoutInflater) }
    @Inject lateinit var adapter: RearrangeAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initOnBackPressedCallback()
		initToolbar()
		initRecyclerView()
	}

	override fun onStart() {
		super.onStart()
		viewModel.onStart()
	}

    override fun onApplyWindowInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        windowWrapper.setupStatusBar(theme, R.attr.scanbot_app_bar_color, false)
        windowWrapper.setupNavigationBar(theme, R.attr.scanbot_window_background, true)

        val insets = windowInsets.getSystemBarsAndDisplayCutoutInsets()
        viewBinding.toolbar.toolbar.setPadding(insets.left, insets.top, insets.right, 0)
        viewBinding.rvScanbotRearrange.setPadding(insets.left, 0, insets.right, insets.bottom)
        viewBinding.rvScanbotRearrange.clipToPadding = false

        val rearrangeMargin = resources.getDimension(R.dimen.scanbot_rearrange_margin).toInt()
        viewBinding.imageView3.updateLayoutParams<MarginLayoutParams> {
            leftMargin = rearrangeMargin + insets.left
        }
        viewBinding.textView3.updateLayoutParams<MarginLayoutParams> {
            rightMargin = rearrangeMargin + insets.right
        }

        return WindowInsetsCompat.CONSUMED
    }

	private fun initOnBackPressedCallback() {
		onBackPressedDispatcher.addCallback(this, true) { viewModel.onBackPressed() }
	}

	private fun initToolbar() = with(viewBinding) {
        toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.tvSave.visibility = View.GONE
        toolbar.tvTitle.text = getString(R.string.scanbot_rearrange_title)
        toolbar.tvTitle.updateLayoutParams<MarginLayoutParams> {
            rightMargin = resources.getDimension(R.dimen.scanbot_back_button_size).toInt()
        }
	}

	private fun initRecyclerView() = with(viewBinding) {
		val rearrangeCallback = RearrangeCallback(viewModel::onPictureDragged)
		val itemTouchHelper = ItemTouchHelper(rearrangeCallback)
		itemTouchHelper.attachToRecyclerView(rvScanbotRearrange)
		rvScanbotRearrange.adapter = adapter
	}

	override fun State.render() = with(viewBinding) {
		pbScanbotRearrange.visibility = if (processing) View.VISIBLE else View.GONE
	}

	override fun Event.handle() = when (this) {
		is ShowItemsEvent -> adapter.setItems(items)
		is ShowErrorMessageEvent -> showMessage(R.string.scanbot_fail)
		is CloseScreenEvent -> finish()
	}
}
