package com.example.newsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.model.Article

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    companion object {
        private val diffUtilCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }
            override fun areContentsTheSame(oldItem: Article, newItem: Article) : Boolean {
                return newItem.title == oldItem.title
            }
        }
    }
    class ArticleViewHolder(var view: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(view.root)
    val differ = AsyncListDiffer(this, diffUtilCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ArticleViewHolder {
        val  inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemArticlePreviewBinding>(inflater, R.layout.item_article_preview, parent, false)
        return ArticleViewHolder(view)
    }
    override fun getItemCount() = differ.currentList.size
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.view.article = article
        // Item CLick Listener
        //Bind these click listeners later
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                article.let { article ->
                    it(article)
                }
            }
        }
        holder.view.ivShare.setOnClickListener {
            onShareNewsClick?.let {
                article?.let { it1 -> it(it1) }
            }
        }
        holder.view.ivSave.setOnClickListener {
            if (holder.view.ivSave.tag.toString().toInt() == 0) {
                holder.view.ivSave.tag = 1
                holder.view.ivSave.setImageDrawable(it.resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                onArticleSaveClick?.let {
                    if (article != null) {
                        it(article)
                    }
                }
            }
            else {
                holder.view.ivSave.tag = 0
                holder.view.ivSave.setImageDrawable(it.resources.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                onArticleDeleteClick?.let {
                    if (article != null) {
                        it(article)
                    }
                }
            }
            onArticleSaveClick?.let {
                article?.let { it1 -> it(it1) }
            }
        }
    }
    var isSave = false
    override fun getItemId(position: Int) = position.toLong()
    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onArticleSaveClick: ((Article) -> Unit)? = null
    private var onArticleDeleteClick: ((Article) -> Unit)? = null
    private var onShareNewsClick: ((Article) -> Unit)? = null
    fun setOnItemCLickListener(listener: ((Article) -> Unit)) {
        onItemClickListener = listener
    }
    fun onSaveClickListener(listener: (Article) -> Unit) {
        onArticleSaveClick = listener
    }
    fun onDeleteClickListener(listener: (Article) -> Unit) {
        onArticleDeleteClick = listener
    }
    fun onShareNewsClick(listener: (Article) -> Unit) {
        onShareNewsClick = listener
    }
}