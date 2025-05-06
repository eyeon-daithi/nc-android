/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.owncloud.android.ui.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.owncloud.android.databinding.GallerySimpleItemBinding
import com.owncloud.android.datamodel.OCFile
import com.owncloud.android.ui.fragment.GalleryFragment
import com.owncloud.android.ui.interfaces.OCFileListFragmentInterface
import com.owncloud.android.utils.glide.CustomGlideStreamLoader

class GallerySimpleItemHolder(
    private val binding: GallerySimpleItemBinding,
    private val thumbnailSize: Int,
    private val getThumbnailUrl: String,
    private val modelLoader: CustomGlideStreamLoader,
    private val ocFileListFragmentInterface: OCFileListFragmentInterface
) : ViewHolder(binding.root) {

    fun bind(file: OCFile) {
        binding.thumbnail.setOnClickListener {
            ocFileListFragmentInterface.onItemClicked(file)
            GalleryFragment.setLastMediaItemPosition(absoluteAdapterPosition)
        }

        Glide
            .with(itemView.context)
            .using(modelLoader)
            .load("$getThumbnailUrl?fileId=${file.localId}&x=$thumbnailSize&y=$thumbnailSize&a=1&mode=cover&forceIcon=0")
            .placeholder(com.elyeproj.loaderviewlibrary.R.color.default_color)
            .override(thumbnailSize, thumbnailSize)
            .into(binding.thumbnail)
    }
}
