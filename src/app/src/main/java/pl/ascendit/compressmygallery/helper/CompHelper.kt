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

package pl.ascendit.compressmygallery.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import java.io.File


object CompHelper {
    val ltag = "CompHelper"

    // constants
    val supportedExtensions = arrayOf("jpg", "JPG", "jpeg", "JPEG", "png", "PNG", "webp", "WEBP")
    val supportedMimeTypes = arrayOf("image/jpg", "image/jpeg", "image/png", "image/webp")
    val version = "1"
    val cmgExt = "cmg"
    lateinit var pref: Pref

    // from preferences
    data class Pref (
        var isAllowedToChangeFormat: Boolean,
        var isAllowedToChangeDimension: Boolean,
        var extension: String,
        var width: Int,
        var quality: Int
    )

    /* returns the path to the compressed image */
    fun compImage(context: Context, path: String): String? {
        val file = File(path)
        var outPath: String? = null

        if ( ! file.exists() ) {
            LogH.d(ltag, "file does not exists ${path}")
            return null
        }

        if ( ! supportedExtensions.contains(file.extension) ) {
            LogH.d(ltag, "extension ${file.extension} is not supported, skipping file ${file.path}")
            return null
        }

        if ( file.name.contains(".$cmgExt") ) {
            if ( ( ! pref.isAllowedToChangeFormat ) || file.extension == pref.extension ) {
                LogH.v(ltag, "file already compressed, skipping ${file.path}")
                return null
            }
        }

        val extension = if ( pref.isAllowedToChangeFormat ) pref.extension else file.extension
        outPath = "${file.parent}/${file.name.substringBefore(".")}.${cmgExt}.${extension}"
        val outFile = File(outPath)

        LogH.v(ltag, "compressing file ${file.path} to ${outPath}")

        if ( ! Compression.compress(file, outFile, quality = pref.quality,
                desiredWidth = if ( pref.isAllowedToChangeDimension ) pref.width else null) ) {
            if ( outFile.exists() ) {
                outFile.delete()
            }
            throw Exception("compression failed, file ${file.path} to ${outPath}")
        }

        val exif = ExifInterface(file)
        val outExif = ExifInterface(outFile)
        outExif.setDateTime(file.lastModified())
        ExifHelper.copyExif(exif, outExif)

        LogH.v(ltag, "removing source file ${file.path}")
        file.delete()

        val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        scanIntent.data = Uri.fromFile(file)
        context.sendBroadcast(scanIntent)

        MediaScannerConnection.scanFile(context,
            arrayOf(outPath),
            arrayOf(extensionToMimeType(outFile.extension)),
            null
        );

        return outPath
    }

    private fun extensionToMimeType(extension: String) : String? {
        return when(extension.lowercase()) {
            "jpg" -> "image/jpg"
            "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> null
        }
    }

}

object Compression {
    private fun extensionToCompressFormat(extension: String) : Bitmap.CompressFormat {
        return when (extension.lowercase()) {
            "png" -> Bitmap.CompressFormat.PNG
            "webp" -> Bitmap.CompressFormat.WEBP
            else -> Bitmap.CompressFormat.JPEG
        }
    }

    fun compress(file: File, outFile: File, quality: Int, desiredWidth: Int?) : Boolean {
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if ( desiredWidth != null && bitmap.width > desiredWidth ) {
            val scale: Double = bitmap.width.toDouble() / desiredWidth.toDouble()
            val newWidth = desiredWidth
            val newHeight = bitmap.height / scale
            bitmap = bitmap.scale(newWidth, newHeight.toInt(), false)
        }
        return bitmap.compress(extensionToCompressFormat(outFile.extension), quality, outFile.outputStream())
    }
}
