package com.kieronquinn.app.monetcompatsample.ui.screens.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentListBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BaseTabFragment
import com.kieronquinn.monetcompat.extensions.views.applyMonetRecursively
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ListFragment: BaseTabFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    companion object {
        private const val KEY_POSITION = "position"
    }

    private val viewModel by viewModel<ListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.applyMonetRecursively()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        setupScrollView(savedInstanceState)
    }

    private fun setupInsets(){
        val bottomNavPadding = resources.getDimension(R.dimen.bottom_nav_height)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root){ view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            binding.root.updatePadding(bottom = (bottomInset + bottomNavPadding).roundToInt())
            insets
        }
    }

    private fun setupScrollView(savedInstanceState: Bundle?) {
        with(binding.root){
            layoutManager = LinearLayoutManager(context).apply {
                onRestoreInstanceState(savedInstanceState?.getParcelable(KEY_POSITION))
            }
            adapter = ListAdapter(context, viewModel.items)
        }
        lifecycleScope.launch {
            containerSharedViewModel.scrollToTopBus.collect {
                if(!isResumed) return@collect
                binding.root.smoothScrollToPosition(0)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(!isAdded) return
        binding.root.layoutManager?.onSaveInstanceState()?.let {
            outState.putParcelable(KEY_POSITION, it)
        }
    }

}