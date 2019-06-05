package jp.gcreate.product.filteredhatebu.ui.filterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.FragmentFilterListBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class FilterListFragment : Fragment() {
    private lateinit var binding: FragmentFilterListBinding
    private val vm: FilterListViewModel by sharedViewModel()
    private val filterListAdapter: FilterListAdapter by inject()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentFilterListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        setupRecyclerView()
        setupActionFromView()
        subscribeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = filterListAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        filterListAdapter.clickEvent.observe(this, Observer {
            Timber.d("filter list clicked: $it")
            it?.handleEvent()?.let { info ->
                val direction = FilterListFragmentDirections
                    .actionNavigationFilterToFilterDetailFragment(info.filter, info.feedCount)
                findNavController().navigate(direction)
            }
        })
    }
    
    private fun setupActionFromView() {
    }
    
    private fun subscribeViewModel() {
        vm.filterInfo.observe(this, Observer { list ->
            list?.let {
                filterListAdapter.submitList(it)
                binding.noContentGroup.isVisible = it.isEmpty()
            }
        })
        vm.deleteFilterEvent.observe(this, Observer {
            it?.handleEvent()?.let {
                Snackbar.make(binding.root, R.string.delete_filter, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.cancel, { vm.undoDeleteFilter() })
                    .show()
            }
        })
        vm.archiveEvent.observe(this, Observer {
            // フィルタにひっかかった記事でも、記事詳細からアーカイブすることができる
            // しかし、記事一覧に移動したときにアーカイブしたメッセージが表示されてしまうので、ここで処理する
            Timber.d("archive feed from filtered detail: ${it?.handleEvent()}")
        })
    }
}
