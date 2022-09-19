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

package pl.ascendit.compressmygallery.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.app.entity.DirItem
import pl.ascendit.compressmygallery.data.log.LogSeverity
import pl.ascendit.compressmygallery.data.log.entity.LogItem
import pl.ascendit.compressmygallery.databinding.DirItemBinding
import pl.ascendit.compressmygallery.databinding.LogItemBinding

class LogItemViewHolder(
    val binding: LogItemBinding,
    val verboseColor: Int,
    val debugColor: Int,
    val errorColor: Int)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(log: LogItem) {
        binding.apply {
            tvLog.text = log.toString()
            if ( log.severity == LogSeverity.VERBOSE ) {
                tvLog.setTextColor(verboseColor)
            } else if ( log.severity == LogSeverity.DEBUG) {
                tvLog.setTextColor(debugColor)
            } else if ( log.severity == LogSeverity.ERROR) {
                tvLog.setTextColor(errorColor)
            }
        }
    }
}

class LogItemAdapter : RecyclerView.Adapter<LogItemViewHolder>() {
    val ltag = "LogItemAdapter"
    var data = mutableListOf<LogItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemViewHolder {
        val binding = LogItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LogItemViewHolder(binding,
        verboseColor = parent.resources.getColor(R.color.log_verbose),
        debugColor = parent.resources.getColor(R.color.log_debug),
        errorColor = parent.resources.getColor(R.color.log_error))
    }

    override fun onBindViewHolder(holder: LogItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun replace(logs: List<LogItem>) {
        data.clear()
        data.addAll(logs)
        sort()
        notifyDataSetChanged()
    }

    private fun sort() {
        data.sortByDescending { it.datetime }
    }
}
