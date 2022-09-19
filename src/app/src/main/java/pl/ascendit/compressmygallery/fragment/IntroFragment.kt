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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.databinding.FragmentIntroBinding


enum class IntroFragmentState {
    GREETING,
    WARRANTY,
    HINT,
    PERMISSION,
}

class IntroFragment : Fragment() {
    private lateinit var binding: FragmentIntroBinding
    var onIntroFinished = {}
    var state = IntroFragmentState.GREETING
    val pageMax = IntroFragmentState.values().size
    private lateinit var tr: Tr

    private class Tr(c: Context) {
        val greetingTitle = c.getString(R.string.intro_greeting_title)
        val greeting = c.getString(R.string.intro_greeting)
        val warrantyTitle = c.getString(R.string.intro_warranty_title)
        val warranty = c.getString(R.string.intro_warranty)
        val hintTitle = c.getString(R.string.intro_hint_title)
        val hint = c.getString(R.string.intro_hint)
        val permissionTitle = c.getString(R.string.intro_permission_title)
        val permission = c.getString(R.string.intro_permission)
        val btNext = c.getString(R.string.bt_next)
        val btAccept = c.getString(R.string.bt_accept)
        val btQuit = c.getString(R.string.bt_quit)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tr = Tr(requireContext())
        return FragmentIntroBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btQuit.text = tr.btQuit

        changeState(state)

        binding.pageIndicator.count = pageMax

        binding.btNext.setOnClickListener {
            next()
        }

        binding.btQuit.setOnClickListener {
            requireActivity().finish()
        }
    }

    fun next() {
        if ( (state.ordinal + 1) >= pageMax ) {
            onIntroFinished()
        } else {
            changeState(IntroFragmentState.values().get(state.ordinal + 1))
        }
    }

    fun previous(): Boolean {
        if ( state.ordinal - 1 >= 0 ) {
            changeState(IntroFragmentState.values().get(state.ordinal - 1))
            return true
        } else {
            return false
        }
    }

    fun changeState(state: IntroFragmentState) {
        this.state = state
        when(state) {
            IntroFragmentState.GREETING -> {
                binding.apply {
                    tvTitle.text = tr.greetingTitle
                    tvDesc.text = tr.greeting
                    btNext.text = tr.btNext
                }
            }
            IntroFragmentState.WARRANTY -> {
                binding.apply {
                    tvTitle.text = tr.warrantyTitle
                    tvDesc.text = tr.warranty
                    btNext.text = tr.btNext
                }
            }
            IntroFragmentState.HINT -> {
                binding.apply {
                    tvTitle.text = tr.hintTitle
                    tvDesc.text = tr.hint
                    btNext.text = tr.btNext
                }
            }
            IntroFragmentState.PERMISSION -> {
                binding.apply {
                    tvTitle.text = tr.permissionTitle
                    tvDesc.text = tr.permission
                    btNext.text = tr.btNext
                }
            }
        }
        setBtQuit()
        setPageIndicator()
    }

    fun setBtQuit() {
        when(state) {
            IntroFragmentState.PERMISSION, IntroFragmentState.HINT -> {
                binding.btQuit.visibility = View.INVISIBLE
            }
            IntroFragmentState.GREETING, IntroFragmentState.WARRANTY -> {
                binding.btQuit.visibility = View.VISIBLE
            }
        }
    }

    fun setPageIndicator() {
        binding.pageIndicator.selection = state.ordinal
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
