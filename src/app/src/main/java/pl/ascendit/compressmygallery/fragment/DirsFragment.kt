/*
 * Copyright (c) 2022 Ascendit Sp. z o. o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/
 *
 */

package pl.ascendit.compressmygallery.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.*
import pl.ascendit.compressmygallery.adapter.DirItemAdapter
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.app.entity.DirItem
import pl.ascendit.compressmygallery.databinding.FragmentDirsBinding
import kotlin.concurrent.thread

class DirsFragment : Fragment() {
    private lateinit var binding: FragmentDirsBinding
    private var adapter: DirItemAdapter? = null
    private val storageHelper = SimpleStorageHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()

        storageHelper.onFolderSelected = { requestCode, folder ->
            val dirFile = folder.toRawDocumentFile(requireContext())
            if ( dirFile == null ) {
                errorToast("Directory is null")
            } else if ( dirFile.exists() ) {
                requireActivity().runOnUiThread() {
                    adapter?.add(DirItem.create(dirFile.getAbsolutePath(requireContext())))
                    binding.tvNoDirs.isVisible = false
                }
            } else {
                errorToast("Directory ${dirFile.getAbsolutePath(requireContext())}\ndoes not exist")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDirsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var funNoDirs = {
            binding.tvNoDirs.visibility = if ( adapter!!.itemCount <= 0 ) View.VISIBLE else View.GONE
        }

        adapter = DirItemAdapter().apply {
            onAdded = { funNoDirs() }
            onRemoved = { funNoDirs() }
            onAllReplaced = { funNoDirs() }
        }

        binding.list.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btAdd.setOnClickListener {
            storageHelper.openFolderPicker()
        }

        binding.btDelete.setOnClickListener {
            adapter!!.removeSelected()
        }
    }

    fun loadData() = thread {
        val data = AppDb.getDirItems()
        requireActivity().runOnUiThread() {
            adapter?.replace(data)
        }
    }

    fun errorToast(errStr: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(context, errStr, Toast.LENGTH_LONG).show()
        }
    }
}
