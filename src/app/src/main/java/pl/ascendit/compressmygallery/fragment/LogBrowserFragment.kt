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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pl.ascendit.compressmygallery.adapter.LogItemAdapter
import pl.ascendit.compressmygallery.data.log.LogDb
import pl.ascendit.compressmygallery.databinding.FragmentLogBrowserBinding
import pl.ascendit.compressmygallery.logic.LogLogic
import kotlin.concurrent.thread


class LogBrowserFragment : Fragment() {
    private lateinit var binding: FragmentLogBrowserBinding
    private var adapter: LogItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLogBrowserBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LogItemAdapter()

        binding.btShare.setOnClickListener {
            thread {
                val text = LogLogic.export()
                requireActivity().runOnUiThread {
                    sendPlainText(text)
                }
            }
        }

        binding.list.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun loadData() = thread {
        val data = LogDb.getLogItems()
        requireActivity().runOnUiThread() {
            adapter?.replace(data)
        }
    }

    fun sendPlainText(text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share logs"))
    }
}
