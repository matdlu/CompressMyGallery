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

package pl.ascendit.compressmygallery.data.comp

import android.content.Context
import kotlinx.coroutines.*
import pl.ascendit.compressmygallery.data.app.AppDao
import pl.ascendit.compressmygallery.data.app.AppDatabase
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.app.entity.DirItem
import pl.ascendit.compressmygallery.data.comp.entity.CompDir
import pl.ascendit.compressmygallery.data.comp.entity.CompFull
import pl.ascendit.compressmygallery.data.comp.entity.CompImg

// Db for persisting UI stuff
object CompDb {
    private lateinit var database: CompDatabase
    private lateinit var dao: CompDao

    fun init(context: Context) {
        database = CompDatabase.getInstance(context)
        dao = database.dao
    }

    /* CompFull */
    fun insertCompFull(compFull: CompFull) {
        dao.insertCompFull(compFull)
    }

    fun getCompFulls() : List<CompFull> {
        return dao.getCompFulls()
    }

    fun countCompFulls(status: CompStatus): Int {
        return dao.countCompFulls(status)
    }

    /* CompDir */
    fun insertCompDir(compDir: CompDir) {
        dao.insertCompDir(compDir)
    }

    fun getCompDirs() : List<CompDir> {
        return dao.getCompDirs()
    }

    fun getCompDirs(compFullId: String) : List<CompDir> {
        return dao.getCompDirs(compFullId)
    }

    /* CompImg */
    fun insertCompImg(compImg: CompImg) {
        dao.insertCompImg(compImg)
    }

    fun getCompImgs() : List<CompImg> {
        return dao.getCompImgs()
    }

    fun getCompImgs(compDirId: String) : List<CompImg> {
        return dao.getCompImgs(compDirId)
    }

    fun countCompImgs(status: CompStatus): Int {
        return dao.countCompImgs(status)
    }

    // returns sum in MB
    fun sizeAfterSum(status: CompStatus): Long {
        return dao.sizeAfterSum(status) / (1024 * 1024)
    }

    // returns sum in MB
    fun sizeBeforeSum(status: CompStatus): Long {
        return dao.sizeBeforeSum(status) / (1024 * 1024)
    }
}

