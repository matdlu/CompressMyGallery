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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.able.CompUpdatable
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.databinding.FragmentCompBinding
import pl.ascendit.compressmygallery.helper.CompHelper
import pl.ascendit.compressmygallery.helper.ImageHelper
import pl.ascendit.compressmygallery.logic.CompLogic
import pl.ascendit.compressmygallery.logic.TopBarLogic
import kotlin.concurrent.thread

enum class CompFragmentState {
    INITIAL,
    STARTED,
    PAUSED,
    FINISHED
}

// Used for preserving UI state when fragment is destroyed
private object Ui {
    var state = CompFragmentState.INITIAL
    var lastImgPath: String = ""
    var lastImgPosition: Int = 0
    var lastImgCnt: Int = 0
    var btStartStopShrinked = true
    var btPickFileShrinked = true
}

class CompFragment : Fragment(), CompUpdatable {
    val ltag = "CompFragment"
    private lateinit var binding: FragmentCompBinding
    lateinit var storageHelper: SimpleStorageHelper
    private lateinit var tr: Tr

    private class Tr(c: Context) {
        val title = c.getString(R.string.comp_title)
        val titleStarted = c.getString(R.string.comp_title_started)
        val titlePaused = c.getString(R.string.comp_title_paused)
        val titleFinished = c.getString(R.string.comp_title_finished)
        val errAddDirFirst = c.getString(R.string.comp_err_add_dir_first)
        val compressed = c.getString(R.string.comp_compressed)
        val photos = c.getString(R.string.comp_photos)
        val of = c.getString(R.string.comp_of)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageHelper.onFileSelected = { requestCode, files ->
            Log.d(ltag, "clicked to compress single file")
            val file = files[0]
            thread {
                CompLogic.runImg(requireContext(), file.getAbsolutePath(requireContext()))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tr = Tr(requireContext())
        return FragmentCompBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeState(Ui.state)

        binding.anywhere.setOnClickListener {
            shrinkBtPickFile()
            shrinkBtStartStop()
        }

        binding.btStartStop.setOnClickListener {
            when (Ui.state) {
                CompFragmentState.INITIAL -> {
                    if ( Ui.btStartStopShrinked ) {
                        extendBtStartStop()
                    } else {
                        shrinkBtStartStop()
                        Log.d(ltag, "clicked to start compression")
                        thread {
                            if (AppDb.getDirItems().size <= 0) {
                                Log.d(ltag, "no saved directories")
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        context,
                                        tr.errAddDirFirst,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                CompLogic.startCompression(requireContext())
                            }
                        }
                    }
                }
                CompFragmentState.STARTED -> {
                    Log.d(ltag, "clicked to pause compression")
                    CompLogic.pauseCompression(requireContext())
                    changeState(CompFragmentState.PAUSED)
                }
                CompFragmentState.PAUSED -> {
                    Log.d(ltag, "clicked to resume compression")
                    CompLogic.resumeCompression(requireContext())
                    changeState(CompFragmentState.STARTED)
                }
                CompFragmentState.FINISHED -> {
                    Log.d(ltag, "clicked to reset view state")
                    changeState(CompFragmentState.INITIAL)
                }
            }
        }

        binding.btPickFile.setOnClickListener {
            if ( Ui.btPickFileShrinked ) {
                extendBtPickFile()
            } else {
                shrinkBtPickFile()
                storageHelper.openFilePicker(filterMimeTypes = CompHelper.supportedMimeTypes)
            }
        }
    }

    fun changeState(state: CompFragmentState) {
        Ui.state = state
        setBtVisibility()
        setOtherViewsVisibility()
        setTvNoComp()
        setBtStartStopIcon()
        setTopBarTitle()
        setImageView()
        setProgress()
    }

    fun setBtVisibility() {
        when(Ui.state) {
            CompFragmentState.INITIAL -> {
                binding.apply {
                    btPickFile.visibility = View.VISIBLE
                    btStartStop.visibility = View.VISIBLE
                }
                shrinkBtPickFile()
                shrinkBtStartStop()
            }
            CompFragmentState.STARTED, CompFragmentState.PAUSED, CompFragmentState.FINISHED -> {
                binding.apply {
                    btPickFile.visibility = View.INVISIBLE
                    btStartStop.visibility = View.VISIBLE
                }
                shrinkBtStartStop()
            }
        }
    }

    fun setOtherViewsVisibilityHelper(visibility: Int) {
        binding.apply {
            ivPhoto.visibility = visibility
            photoLabel.visibility = visibility
            tvPhotoPath.visibility = visibility
            tvProgressInfo.visibility = visibility
            progressBar.visibility = visibility
        }
    }

    fun setOtherViewsVisibility() {
        when(Ui.state) {
            CompFragmentState.INITIAL -> {
                setOtherViewsVisibilityHelper(View.INVISIBLE)
            }
            CompFragmentState.STARTED, CompFragmentState.PAUSED, CompFragmentState.FINISHED -> {
                setOtherViewsVisibilityHelper(View.VISIBLE)
            }
        }
    }

    fun setTvNoComp() {
        binding.apply {
            when(Ui.state) {
                CompFragmentState.INITIAL -> {
                    //tvNoComp.text = tr.noComp
                    tvNoComp.visibility = View.VISIBLE
                }
                CompFragmentState.STARTED, CompFragmentState.PAUSED, CompFragmentState.FINISHED -> {
                    tvNoComp.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun setTopBarTitle() {
        binding.apply {
            when(Ui.state) {
                CompFragmentState.INITIAL -> {
                    TopBarLogic.updateTitle(tr.title)
                }
                CompFragmentState.STARTED -> {
                    TopBarLogic.updateTitle(tr.titleStarted)
                }
                CompFragmentState.PAUSED -> {
                    TopBarLogic.updateTitle(tr.titlePaused)
                }
                CompFragmentState.FINISHED -> {
                    TopBarLogic.updateTitle(tr.titleFinished)
                }
            }
        }
    }

    fun setBtStartStopIcon() {
        binding.apply {
            when(Ui.state) {
                CompFragmentState.STARTED -> {
                    btStartStop.setIconResource(R.drawable.ic_baseline_pause_24)
                }
                CompFragmentState.INITIAL, CompFragmentState.PAUSED -> {
                    btStartStop.setIconResource(R.drawable.ic_baseline_play_arrow_24)
                }
                CompFragmentState.FINISHED -> {
                    btStartStop.setIconResource(R.drawable.ic_baseline_replay_24)
                }
            }
        }
    }

    fun setImageView() {
        when(Ui.state) {
            CompFragmentState.INITIAL -> {
                Ui.lastImgPath = ""
            }
            CompFragmentState.STARTED, CompFragmentState.PAUSED, CompFragmentState.FINISHED -> {
                updateImage(Ui.lastImgPath)
            }
        }
    }

    fun setProgress() {
        when(Ui.state) {
            CompFragmentState.INITIAL -> {
                Ui.lastImgPosition = 0
                Ui.lastImgCnt = 0
            }
            CompFragmentState.STARTED, CompFragmentState.PAUSED, CompFragmentState.FINISHED -> {
                updateProgress(Ui.lastImgPosition, Ui.lastImgCnt)
            }
        }
    }

    fun shrinkBtStartStop() {
        binding.btStartStop.shrink()
        Ui.btStartStopShrinked = true
    }

    fun extendBtStartStop() {
        binding.btStartStop.extend()
        Ui.btStartStopShrinked = false
    }

    fun shrinkBtPickFile() {
        binding.btPickFile.shrink()
        Ui.btPickFileShrinked = true
    }

    fun extendBtPickFile() {
        binding.btPickFile.extend()
        Ui.btPickFileShrinked = false
    }

    /* Updatable */
    override fun start(imgPath: String) {
        Ui.lastImgPath = imgPath
        if ( isAdded ) {
            requireActivity().runOnUiThread {
                changeState(CompFragmentState.STARTED)
            }
        } else {
            Ui.state = CompFragmentState.STARTED
        }
    }

    override fun finish() {
        if ( isAdded ) {
            requireActivity().runOnUiThread {
                changeState(CompFragmentState.FINISHED)
            }
        } else {
            Ui.state = CompFragmentState.FINISHED
        }
    }

    override fun updateImage(path: String) {
        Ui.lastImgPath = path
        if ( isAdded ) {
            requireActivity().runOnUiThread {
                if (path.isEmpty()) {
                    Log.e(ltag, "image preview cannot be loaded because path is empty")
                    binding.tvPhotoPath.text = "Brak ścieżki"
                } else {
                    Log.d(ltag, "image preview set to: ${path}")
                    binding.tvPhotoPath.text = path
                    ImageHelper.setImg(binding.ivPhoto, path)
                }
            }
        }
    }

    override fun updateProgress(imgPosition: Int, imgCnt: Int) {
        Ui.lastImgCnt = imgCnt
        Ui.lastImgPosition = imgPosition
        if ( isAdded ) {
            requireActivity().runOnUiThread {
                binding.tvProgressInfo.text = "${tr.compressed} ${imgPosition} ${tr.of} ${imgCnt} ${tr.photos}"
                val perc = (imgPosition.toDouble() / imgCnt.toDouble()) * 100.0
                Log.v(ltag, "progress bar set to ${perc}")
                binding.progressBar.setProgress(perc.toInt(), true)
            }
        }
    }

    override fun errorToast(errStr: String) {
        if ( isAdded ) {
            requireActivity().runOnUiThread {
                makeText(context, errStr, Toast.LENGTH_LONG).show()
            }
        }
    }
}
