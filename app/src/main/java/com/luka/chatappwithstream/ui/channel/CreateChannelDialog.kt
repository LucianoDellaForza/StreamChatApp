package com.luka.chatappwithstream.ui.channel

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luka.chatappwithstream.R
import com.luka.chatappwithstream.databinding.DialogChannelNameBinding
import dagger.hilt.android.AndroidEntryPoint

//DialogFragment - internally is fragment which makes it easier to navigate to it with nav component, inject dependencies...
@AndroidEntryPoint
class CreateChannelDialog : DialogFragment() {

    private var _binding: DialogChannelNameBinding? = null
    private val binding: DialogChannelNameBinding
        get() = _binding!!

    private val viewModel: ChannelViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogChannelNameBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_channel_name)
            .setView(binding.root)
            .setPositiveButton(R.string.create) { _, _ ->
                viewModel.createChannel(binding.etChannelName.text.toString())
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}