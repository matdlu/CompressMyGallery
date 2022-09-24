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

package pl.ascendit.compressmygallery.data.log.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.ascendit.compressmygallery.data.log.LogDb
import pl.ascendit.compressmygallery.data.log.LogSeverity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
data class LogItem (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val datetime: LocalDateTime = LocalDateTime.now(),
    val tag: String,
    val severity: LogSeverity,
    val message: String
) {
    companion object {
        fun create(tag: String, severity: LogSeverity, message: String) : LogItem {
            return LogItem(tag = tag, severity = severity, message = message).apply {
                LogDb.insertLogItem(this)
            }
        }

        private val dateFormatter =  DateTimeFormatter.ISO_LOCAL_DATE
        private val timeFormatter =  DateTimeFormatter.ISO_LOCAL_TIME

        private fun formatSeverity(severity: LogSeverity) : Char {
            return severity.toString().get(0)
        }

        private fun formatDatetime(datetime: LocalDateTime) : String {
            return datetime.format(dateFormatter) + " " + datetime.format(timeFormatter)
        }
    }


    override fun toString(): String {
        return "${formatDatetime(datetime)} ${formatSeverity(severity)}/${tag}: ${message}"
    }
}