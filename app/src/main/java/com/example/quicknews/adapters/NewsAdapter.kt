package com.example.quicknews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.models.Article

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private lateinit var articleImage: ImageView
    private lateinit var articleSource: TextView
    private lateinit var articleDescription: TextView
    private lateinit var articleTitle: TextView
    private lateinit var articleDateTime: TextView

    //To efficiently update the content of recycle view with refreshing the entire dataset.
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemCount(): Int {
        //return the size of list
        return differ.currentList.size
    }

    private var onItemClickListener:((Article) -> Unit) ?= null
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        //Bind data with view
        val article = differ.currentList[position]

        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)

        holder.itemView.apply {
            //For handling image data to load image from url into articleImage view
            Glide.with(this).load(article.urlToImage).into(articleImage)
            articleSource.text = article.source?.name
            articleTitle.text = article.title
            articleDescription.text = article.description.toString()
            articleDateTime.text = article.publishedAt

            //If any item is clicked then it open article.
            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }


}