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
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.comp.CompDb
import pl.ascendit.compressmygallery.data.comp.CompStatus
import pl.ascendit.compressmygallery.logic.CompLogic
import java.time.LocalDateTime

@Entity
data class CompFull (
    val compDirIds: MutableList<String> = mutableListOf<String>(), // required
    @PrimaryKey(autoGenerate = false)
    val id: String = genId(),
    var timeStarted: LocalDateTime? = null,
    var timeEnded: LocalDateTime? = null,
    var status: CompStatus = CompStatus.AWAITING,
) {
    companion object {
        private val ltag = "CompFull"

        private fun genId() : String {
            return LocalDateTime.now().hashCode().toString()
        }

        fun create() : CompFull? {
            Log.v(ltag, "creating CompFull")
            return CompFull().apply {
                val dirs = AppDb.getDirItems()
                for (dir in dirs) {
                    val compDir = CompDir.create(id, dir.path)
                    if (compDir != null) {
                        Log.d(ltag, "${compDir.pathToDir} added")
                        compDirIds.add(compDir.id)
                    }
                }
                if ( compDirIds.count() <= 0 ) {
                    CompLogic.updatable.errorToast("No directories with images to compress")
                    Log.d(ltag, "no directories with images to compress")
                    return null
                }
                CompDb.insertCompFull(this)
            }
        }
    }
}
