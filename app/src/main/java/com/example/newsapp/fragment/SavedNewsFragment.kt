package com.example.newsapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.SavedNewsAdapter
import com.example.newsapp.utils.shareNews
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel:NewsViewModel
    lateinit var newsAdapter: SavedNewsAdapter
    val TAG = "SavedNewsFragment"
    private fun setupRecyclerView() {
        newsAdapter = SavedNewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        newsAdapter.setOnItemCLickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        newsAdapter.onShareNewsClick {
            shareNews(context, it)
        }
        // swipe to delete
        val onItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or  ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        )
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(), "Deleted Successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insertArticle(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(onItemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        setViewModelObserver()
    }
    private fun setViewModelObserver() {
        viewModel = (activity as MainActivity).viewModel
        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer {
            Log.i(TAG,""+it.size)
            newsAdapter.differ.submitList(it)
            rvSavedNews.visibility = View.VISIBLE
            shimmerFrameLayout2.stopShimmerAnimation()
            shimmerFrameLayout2.visibility = View.GONE
        })
    }

}