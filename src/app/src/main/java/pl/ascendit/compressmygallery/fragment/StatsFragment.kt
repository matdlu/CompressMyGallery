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

import android.content.Context
import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.data.comp.CompDb
import pl.ascendit.compressmygallery.data.comp.CompStatus
import pl.ascendit.compressmygallery.databinding.FragmentStatsBinding
import kotlin.concurrent.thread

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStatsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun loadData() = thread {
        val runCount = CompDb.countCompFulls(CompStatus.COMPLETED)
        val imgCount = CompDb.countCompImgs(CompStatus.COMPLETED)
        val sizeAfterSum = CompDb.sizeAfterSum(CompStatus.COMPLETED)
        val sizeBeforeSum = CompDb.sizeBeforeSum(CompStatus.COMPLETED)
        val freedSpacePerc: Double? = if ( sizeBeforeSum != 0L ) sizeAfterSum.toDouble() / sizeBeforeSum.toDouble() else null
        requireActivity().runOnUiThread {
            binding.apply {
                tvCompRunCount.text = runCount.toString()
                tvCompPhotoCount.text = imgCount.toString()
                tvCompFreedSpaceSum.text = "${sizeBeforeSum - sizeAfterSum} MB"
                tvCompFreedSpacePerc.text = if (freedSpacePerc != null) "${DecimalFormat("##").format((1 - freedSpacePerc) * 100)}%" else "0%"
            }
        }
    }
}