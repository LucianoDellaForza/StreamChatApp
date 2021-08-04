package com.luka.chatappwithstream.ui.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.luka.chatappwithstream.R
import com.luka.chatappwithstream.databinding.FragmentChannelBinding
import com.luka.chatappwithstream.ui.BindingFragment
import com.luka.chatappwithstream.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChannelFragment : BindingFragment<FragmentChannelBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelBinding::inflate

    private val viewModel: ChannelViewModel by activityViewModels() //bind view model to activity lifecycle (something to do with dialog of creating new channel)

    // initialize all views in here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //check if we are logged in
        val user = viewModel.getUser()
        if (user == null) {
            findNavController().popBackStack()
            return
        }

        //Stream views have their own view models - viewModel require factory that contain query which channels to fetch
        //for ChannelList view
        val factory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                //Filters.eq("members", listOf(memberId's')) - this is how to get all message type chats for concrete users
            ),
            sort = ChannelListViewModel.DEFAULT_SORT,
            limit = 30  //load 30 channels at once
        )
        val channelListViewModel: ChannelListViewModel by viewModels { factory } //this is how to pass custom factory to viewModel
        //for ChannelHeader view
       val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        //bind viewModels to views
        channelListViewModel.bindView(binding.channelListView, viewLifecycleOwner)
        channelListHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)

        //logout functionality
        binding.channelListHeaderView.setOnUserAvatarClickListener {
            viewModel.logout()
            findNavController().popBackStack()
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            findNavController().navigateSafely(
                R.id.action_channelFragment_to_createChannelDialog
            )
        }

        binding.channelListView.setChannelItemClickListener { channel ->
            findNavController().navigateSafely(
                R.id.action_channelFragment_to_chatFragment,
                Bundle().apply {
                    putString("channelId", channel.cid)
                }
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.createChannelEvent.collect { event ->
                when (event) {
                    is ChannelViewModel.CreateChannelEvent.Error -> {
                        Toast.makeText(
                            requireContext(),
                            event.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is ChannelViewModel.CreateChannelEvent.Success -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.channel_created,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

}