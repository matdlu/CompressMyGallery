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

import android.util.Log
import androidx.core.text.isDigitsOnly
import pl.ascendit.compressmygallery.logic.SettingsLogic

class ValidationHelper(
    val onError: (str: String?, message: String) -> Unit
) {
    private val ltag = "ValidationHelper"
    private val basic = BasicValidation()

    fun string(str: String?) : Boolean {
        return basic.notNull(str) && basic.notEmpty(str!!)
    }

    fun number(str: String?) : Boolean {
        return string(str) && basic.isDigitOnly(str!!)
    }

    fun positiveNumber(str: String?) : Boolean {
        return number(str) && basic.isPositiveNumber(str!!)
    }

    fun positiveNumberBetween(str: String?, low: Int, high: Int) : Boolean {
        return positiveNumber(str) && basic.between(str!!, low, high)
    }

    inner class BasicValidation {
        private fun handleError(str: String?, errStr: String) {
            Log.d(ltag, errStr)
            onError(str, errStr)
        }

        fun notNull(str: String?) : Boolean {
            if (str == null) {
                handleError(str, "value is null")
                return false
            }
            return true
        }

        fun notEmpty(str: String) : Boolean{
            if (str.isEmpty()) {
                handleError(str, "value is empty")
                return false
            }
            return true
        }

        fun isDigitOnly(str : String) : Boolean {
            if ( ! str.isDigitsOnly() ) {
                handleError(str, "value is not digit only: $str")
                return false
            }
            return true
        }

        fun isPositiveNumber(str: String) : Boolean {
            try {
                val value = Integer.parseInt(str)
                if (value < 0) {
                    handleError(str, "value is not a positive number: $str")
                    return false
                }
            } catch (e: Exception) {
                handleError(str, "Exception on value: $str, message: ${e.message}")
                return false
            }
            return true
        }

        fun between(str: String, low: Int, high: Int) : Boolean {
            val value = Integer.parseInt(str)
            if ( value >= low && value <= high ) {
                return true
            } else {
                handleError(str, "value is not between $low and $high")
                return false
            }
        }
    }
}
