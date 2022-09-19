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

package pl.ascendit.compressmygallery.data.comp.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.ascendit.compressmygallery.data.comp.CompDb
import pl.ascendit.compressmygallery.data.comp.CompStatus
import java.time.LocalDateTime

@Entity
data class CompImg (
    val compDirId: String? = null, // optional
    var path: String = "", // required
    val sizeBefore: Long = 0L, // required
    @PrimaryKey(autoGenerate = false)
    val id: String = genId(),
    var sizeAfter: Long = 0L,
    var status: CompStatus = CompStatus.AWAITING,
    var errStr: String = "",
) {
    companion object {
        private val ltag = "CompImg"

        private fun genId() : String {
            return LocalDateTime.now().hashCode().toString()
        }

        fun create(compDirId: String, path: String, size: Long) : CompImg {
            Log.v(ltag, "creating CompImg")
            return CompImg(compDirId = compDirId, path = path, sizeBefore = size).apply {
                CompDb.insertCompImg(this)
            }
        }

        /* When compressing single image */
        fun create(path: String, size: Long) : CompImg {
            Log.v(ltag, "creating CompImg")
            return CompImg(path = path, sizeBefore = size).apply {
                CompDb.insertCompImg(this)
            }
        }
    }
}
