/*
 * IONOS HiDrive Next - Android Client
 *
 * SPDX-FileCopyrightText: 2025 STRATO GmbH.
 * SPDX-License-Identifier: GPL-2.0
 */

package com.owncloud.android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextcloud.client.account.User
import com.nextcloud.client.network.ClientFactoryImpl
import com.nextcloud.client.preferences.AppPreferences
import com.owncloud.android.MainApp
import com.owncloud.android.databinding.GalleryHeaderBinding
import com.owncloud.android.databinding.GallerySimpleItemBinding
import com.owncloud.android.datamodel.FileDataStorageManager
import com.owncloud.android.datamodel.OCFile
import com.owncloud.android.ui.activity.ComponentsGetter
import com.owncloud.android.ui.fragment.GalleryFragment
import com.owncloud.android.ui.fragment.GalleryFragmentBottomSheetDialog
import com.owncloud.android.ui.fragment.SearchType
import com.owncloud.android.ui.interfaces.OCFileListFragmentInterface
import com.owncloud.android.utils.DisplayUtils
import com.owncloud.android.utils.FileSortOrder
import com.owncloud.android.utils.MimeTypeUtil
import com.owncloud.android.utils.glide.CustomGlideStreamLoader
import com.owncloud.android.utils.theme.ViewThemeUtils

class GallerySimpleAdapter(
    context: Context,
    user: User,
    private val ocFileListFragmentInterface: OCFileListFragmentInterface,
    preferences: AppPreferences,
    transferServiceGetter: ComponentsGetter,
    viewThemeUtils: ViewThemeUtils,
    private var columns: Int,
    private val defaultThumbnailSize: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CommonOCFileListAdapterInterface {

    private val storageManager = transferServiceGetter.storageManager
    private val clientFactory = ClientFactoryImpl(MainApp.getAppContext())
    private val getThumbnailUrl = clientFactory.create(user).baseUri.toString() + "/index.php/core/preview"
    private val modelLoader = CustomGlideStreamLoader(user, clientFactory)

    private val ocFileListDelegate = OCFileListDelegate(
        transferServiceGetter.fileUploaderHelper,
        context,
        ocFileListFragmentInterface,
        user,
        storageManager,
        false,
        preferences,
        true,
        transferServiceGetter,
        showMetadata = false,
        showShareAvatar = false,
        viewThemeUtils
    )

    private val items: MutableList<GalleryItem> = mutableListOf()

    private var layoutManager: GridLayoutManager? = null

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    fun showAllGalleryItems(
        remotePath: String,
        mediaState: GalleryFragmentBottomSheetDialog.MediaState,
        photoFragment: GalleryFragment,
    ) {
        val files = storageManager.allGalleryItems
            .filter { it != null && it.remotePath.startsWith(remotePath) && it.isSatisfiesMediaState(mediaState) }
            .distinct()
            .sortedByDescending { it.modificationTimestamp }

        if (files.isEmpty()) {
            photoFragment.setEmptyListMessage(SearchType.GALLERY_SEARCH)
        }

        val filesGroupedByMonthAndYear = files.groupBy { file ->
            val month = DisplayUtils.getDateByPattern(file.modificationTimestamp, DisplayUtils.MONTH_PATTERN)
            val year = DisplayUtils.getDateByPattern(file.modificationTimestamp, DisplayUtils.YEAR_PATTERN)
            month to year
        }

        items.clear()
        filesGroupedByMonthAndYear.forEach { (month, year), files ->
            items.add(GalleryItem.Header(month, year))
            items.addAll(files.map(GalleryItem::FileItem))
        }

        notifyDataSetChanged()
    }

    private fun OCFile.isSatisfiesMediaState(mediaState: GalleryFragmentBottomSheetDialog.MediaState): Boolean {
        return when (mediaState) {
            GalleryFragmentBottomSheetDialog.MediaState.MEDIA_STATE_PHOTOS_ONLY -> MimeTypeUtil.isImage(mimeType)
            GalleryFragmentBottomSheetDialog.MediaState.MEDIA_STATE_VIDEOS_ONLY -> MimeTypeUtil.isVideo(mimeType)
            GalleryFragmentBottomSheetDialog.MediaState.MEDIA_STATE_DEFAULT -> true
        }
    }

    fun setLayoutManager(layoutManager: GridLayoutManager) {
        layoutManager.spanCount = columns
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == VIEW_TYPE_HEADER) {
                    layoutManager.spanCount
                } else {
                    1
                }
            }
        }
        this.layoutManager = layoutManager
    }

    fun changeColumn(newColumn: Int) {
        this.columns = newColumn
        this.layoutManager?.spanCount = newColumn
    }

    fun getItem(position: Int): OCFile? {
        return (items[position] as? GalleryItem.FileItem)?.file
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is GalleryItem.Header -> VIEW_TYPE_HEADER
            is GalleryItem.FileItem -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val binding = GalleryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GalleryHeaderViewHolder(binding)
        } else {
            val binding = GallerySimpleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return GallerySimpleItemHolder(
                binding,
                defaultThumbnailSize,
                getThumbnailUrl,
                modelLoader,
                ocFileListFragmentInterface,
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is GalleryItem.Header -> (holder as GalleryHeaderViewHolder).let {
                it.binding.month.text = item.month
                it.binding.year.text = item.year
            }

            is GalleryItem.FileItem -> (holder as GallerySimpleItemHolder).bind(item.file)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun isMultiSelect(): Boolean {
        return ocFileListDelegate.isMultiSelect
    }

    override fun cancelAllPendingTasks() {
        ocFileListDelegate.cancelAllPendingTasks()
    }

    override fun getItemPosition(file: OCFile): Int {
        return items.indexOfFirst { it is GalleryItem.FileItem && it.file == file }
    }

    override fun swapDirectory(
        user: User,
        directory: OCFile,
        storageManager: FileDataStorageManager,
        onlyOnDevice: Boolean,
        mLimitToMimeType: String
    ) {
        // TODO("Not yet implemented")
    }

    override fun setHighlightedItem(file: OCFile) {
        // TODO("Not yet implemented")
    }

    override fun setSortOrder(mFile: OCFile, sortOrder: FileSortOrder) {
        // TODO("Not yet implemented")
    }

    override fun addCheckedFile(file: OCFile) {
        ocFileListDelegate.addCheckedFile(file)
    }

    override fun isCheckedFile(file: OCFile): Boolean {
        return ocFileListDelegate.isCheckedFile(file)
    }

    override fun getCheckedItems(): Set<OCFile> {
        return ocFileListDelegate.checkedItems
    }

    override fun removeCheckedFile(file: OCFile) {
        ocFileListDelegate.removeCheckedFile(file)
    }

    override fun notifyItemChanged(file: OCFile) {
        notifyItemChanged(getItemPosition(file))
    }

    override fun getFilesCount(): Int {
        return items.size
    }

    override fun setMultiSelect(boolean: Boolean) {
        ocFileListDelegate.isMultiSelect = boolean
        notifyDataSetChanged()
    }

    override fun clearCheckedItems() {
        ocFileListDelegate.clearCheckedItems()
    }

    sealed class GalleryItem {
        data class Header(val month: String, val year: String) : GalleryItem()
        data class FileItem(val file: OCFile) : GalleryItem()
    }
}
