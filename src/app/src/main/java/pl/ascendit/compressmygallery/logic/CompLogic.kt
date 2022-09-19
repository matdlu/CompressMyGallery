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

package pl.ascendit.compressmygallery.logic

import android.content.Context
import pl.ascendit.compressmygallery.able.CompUpdatable
import pl.ascendit.compressmygallery.data.comp.*
import pl.ascendit.compressmygallery.data.comp.entity.CompFull
import pl.ascendit.compressmygallery.data.comp.CompStatus
import pl.ascendit.compressmygallery.data.comp.entity.CompDir
import pl.ascendit.compressmygallery.data.comp.entity.CompImg
import pl.ascendit.compressmygallery.helper.CompHelper
import pl.ascendit.compressmygallery.helper.LogH
import java.io.File
import kotlin.concurrent.thread

object CompLogic {
    val ltag = "CompLogic"
    var isCompRunning = false
    lateinit var updatable: CompUpdatable

    /* compress all directories */
    fun runFull(context: Context, compFull: CompFull) {
        if (! isCompRunning) {
            LogH.e(ltag, "cannot start full run, isCompRunning is false ${compFull.id}")
            return
        }

        LogH.d(ltag, "started full run ${compFull.id}")
        compFull.status = CompStatus.ONGOING
        CompDb.insertCompFull(compFull)

        val compDirs = CompDb.getCompDirs(compFull.id)

        var i = 0
        var imgCnt = 0
        for(compDir in compDirs) {
            imgCnt += compDir.compImgIds.size
        }

        var updatableStarted = false
        for(compDir in compDirs) {
            if ( compDir.status == CompStatus.COMPLETED ) {
                LogH.d(ltag, "skipping dir run ${compDir.pathToDir}")
                continue
            }

            LogH.d(ltag, "started dir run ${compDir.pathToDir}")
            compDir.status = CompStatus.ONGOING
            CompDb.insertCompDir(compDir)

            for(compImg in CompDb.getCompImgs(compDir.id)) {
                updatable.updateProgress(++i, imgCnt)

                if ( compDir.status == CompStatus.COMPLETED ) {
                    LogH.d(ltag, "skipping img run ${compDir.pathToDir}")
                    continue
                }

                if (! isCompRunning) {
                    LogH.v(ltag, "compression stopped")

                    compImg.status = CompStatus.STOPPED
                    compDir.status = CompStatus.STOPPED
                    compFull.status = CompStatus.STOPPED
                    updateAll(compFull, compDir, compImg)

                    var secCnt = 0
                    while( ! isCompRunning ) {
                        Thread.sleep(1000)
                        secCnt++
                        if ( secCnt % 60 == 0 ) {
                            LogH.v(ltag, "waiting until compression will be resumed, so far for ${secCnt / 60} minutes")
                        }
                    }

                    compImg.status = CompStatus.ONGOING
                    compDir.status = CompStatus.ONGOING
                    compFull.status = CompStatus.ONGOING
                    updateAll(compFull, compDir, compImg)
                }

                try {
                    LogH.d(ltag, "started img run ${compImg.path}")
                    compImg.status = CompStatus.ONGOING
                    CompDb.insertCompImg(compImg)
                    if ( ! updatableStarted ) { // make sure it's started once
                        updatable.start(compImg.path)
                        updatableStarted = true
                    }
                    compImgOngoing(context, compImg)
                } catch (e: Exception) {
                    handleImgError(e, compImg)
                    continue
                }
            }

            compDir.status = CompStatus.COMPLETED
            CompDb.insertCompDir(compDir)
        }

        compFull.status = CompStatus.COMPLETED
        CompDb.insertCompFull(compFull)
    }

    private fun updateAll(compFull: CompFull, compDir: CompDir, compImg: CompImg) {
        CompDb.insertCompImg(compImg)
        CompDb.insertCompDir(compDir)
        CompDb.insertCompFull(compFull)
    }

    /* compress a single image */
    fun runImg(context: Context, imgPath: String) {
        isCompRunning = true
        val file = File(imgPath)
        if ( ! file.exists() ) {
            isCompRunning = false
            LogH.d(ltag, "file does not exists ${imgPath}")
            return
        }
        val compImg = CompImg.create(imgPath, file.length())
        try {
            LogH.d(ltag, "started img run ${compImg.path}")
            updatable.updateProgress(0, 1)
            compImg.status = CompStatus.ONGOING
            CompDb.insertCompImg(compImg)
            updatable.start(compImg.path)
            compImgOngoing(context, compImg)
            isCompRunning = false
            updatable.updateProgress(1, 1)
            updatable.finish()
        } catch (e: Exception) {
            handleImgError(e, compImg)
            isCompRunning = false
        }
    }

    private fun compImgOngoing(context: Context, compImg: CompImg) : Boolean {
        val outPath = CompHelper.compImage(context, compImg.path)
        if ( outPath != null ) {
            compImg.path = outPath
            compImg.sizeAfter = File(outPath).length()
            compImg.status = CompStatus.COMPLETED
            CompDb.insertCompImg(compImg)
            updatable.updateImage(outPath)
            return true
        }
        return false
    }

    private fun handleImgError(e: Exception, compImg: CompImg) {
        LogH.e(ltag, e.message.toString())
        compImg.status = CompStatus.ERROR
        compImg.errStr = e.message.toString()
        CompDb.insertCompImg(compImg)
        updatable.errorToast(e.message.toString())
    }

    fun startCompression(context: Context) {
        if ( ! isCompRunning ) {
            thread {
                val compFull = CompFull.create()
                if (compFull != null) {
                    isCompRunning = true
                    runFull(context, compFull)
                    updatable.finish()
                }
                isCompRunning = false
            }
        } else {
            LogH.e(ltag, "tried to start compression when it's already running")
        }
    }

    fun resumeCompression(context: Context) {
        isCompRunning = true
    }

    fun pauseCompression(context: Context) {
        isCompRunning = false
    }
}