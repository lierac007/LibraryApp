package activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.Book
import com.example.bookapp.BookAdapter
import com.example.bookapp.R
import com.google.firebase.database.*

class DisplayBookData : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadData: TextView
    private lateinit var dbReference: DatabaseReference
    private var bookList: ArrayList<Book> = arrayListOf()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.display_book)

        recyclerView = findViewById(R.id.recyclerView)
        loadData = findViewById(R.id.loadData)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dbReference = FirebaseDatabase.getInstance().getReference("Books")

        //  Ensure bookId is passed correctly!
        bookAdapter = BookAdapter(bookList) { selectedBook, bookId ->
            Toast.makeText(this, "Clicked on ${selectedBook.bookName} (ID: $bookId)", Toast.LENGTH_SHORT).show()

            val fragment = BookDetailsFragment.newInstance(selectedBook, bookId)
            fragment.show(supportFragmentManager, "BookDetailsFragment")
        }
        recyclerView.adapter = bookAdapter

        getBookData()
    }

    private fun getBookData() {
        recyclerView.visibility = View.GONE
        loadData.visibility = View.VISIBLE

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                if (snapshot.exists()) {
                    for (bookSnap in snapshot.children) {
                        val bookData = bookSnap.getValue(Book::class.java)
                        val bookId = bookSnap.key
                        if (bookData != null && bookId != null) {
                            bookData.bookId = bookId // Assign bookId to the Book object
                            bookList.add(bookData)
                        }
                    }
                    bookAdapter.notifyDataSetChanged() // Notify adapter to update RecyclerView
                    recyclerView.visibility = View.VISIBLE
                    loadData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DisplayBookData, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}
