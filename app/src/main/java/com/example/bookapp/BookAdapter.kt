package com.example.bookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val bookList: ArrayList<Book>,
    private val onItemClick: (Book, String) -> Unit  // Expect bookId as a second argument
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tvBookTitle)
        val authorTextView: TextView = view.findViewById(R.id.tvBookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        val bookId = book.bookId ?: ""

        holder.titleTextView.text = book.bookName ?: "No Title"
        holder.authorTextView.text = book.authorName ?: "No Author"

        // Pass both `book` and `bookId` on click
        holder.itemView.setOnClickListener { onItemClick(book, bookId) }
    }

    override fun getItemCount() = bookList.size
}


