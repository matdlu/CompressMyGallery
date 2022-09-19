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
import androidx.room.Ignore
import androidx.room.PrimaryKey
import pl.ascendit.compressmygallery.data.comp.CompDb
import pl.ascendit.compressmygallery.data.comp.CompStatus
import pl.ascendit.compressmygallery.logic.CompLogic
import java.io.File
import java.time.LocalDateTime

@Entity
data class CompDir (
    var compFullId: String = "", // required
    var pathToDir: String = "", // required
    var compImgIds: MutableList<String> = mutableListOf<String>(), // required
    @PrimaryKey(autoGenerate = false)
    var id: String = genId(),
    var status: CompStatus = CompStatus.AWAITING,
    @Ignore
    private var compImgs: List<CompImg>? = null
) {
    companion object {
        private val ltag = "CompDir"

        private fun genId() : String {
            return LocalDateTime.now().hashCode().toString()
        }

        fun create(compFullId: String, dirPath: String) : CompDir? {
            Log.v(ltag, "creating CompDir")
            return CompDir(compFullId = compFullId, pathToDir = dirPath).apply {
                val dir = File(dirPath)
                if (!dir.exists()) {
                    CompLogic.updatable.errorToast("Directory ${dirPath}\ndoes not exist")
                    Log.e(ltag, "dir does not exist ${dirPath}")
                    return null
                }
                val files = dir.listFiles()
                if (files == null) {
                    Log.d(ltag, "dir is empty ${dirPath}")
                    return null
                }
                for (file in files) {
                    val compImg = CompImg.create(id, file.path, file.length())
                    compImgIds.add(compImg.id)
                }
                CompDb.insertCompDir(this)
            }
        }
    }
}
