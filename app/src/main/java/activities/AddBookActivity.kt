package activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.BookData
import com.example.bookapp.R
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddBookActivity : AppCompatActivity() {

    private lateinit var bookNameEditText: EditText
    private lateinit var authorNameEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var dataBaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_book) // Link to the second XML layout
        FirebaseApp.initializeApp(this)

        bookNameEditText = findViewById(R.id.BookNameEditText)
        authorNameEditText = findViewById(R.id.AuthorNameEditText)
        submitButton = findViewById(R.id.Submit)


        dataBaseRef = FirebaseDatabase.getInstance(
            "https://booking-15165-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("Books")

        submitButton.setOnClickListener {
            Log.d("AddBookActivity", "Submit button clicked")
            saveBookData()
            clearInputFields()

        }
    }

    private fun clearInputFields() {
        bookNameEditText.text.clear()
        authorNameEditText.text.clear()
    }

    private fun saveBookData() {
        val bookName = bookNameEditText.text.toString()
        val authorName = authorNameEditText.text.toString()

        if (bookName.isEmpty()) {
            bookNameEditText.error = "Please enter book name"
            return
        }
        if (authorName.isEmpty()) {
            authorNameEditText.error = "Please enter author name"
            return
        }

        val bookId = dataBaseRef.push().key ?: run {
            Log.e("FirebaseError", "Failed to generate book ID")
            return
        }

        val book = BookData(bookId, bookName, authorName)

        dataBaseRef.child(bookId).setValue(book)
            .addOnSuccessListener {
            Log.d("FirebaseSuccess", "Book saved successfully!")
            Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show()
        }


            .addOnFailureListener { err ->
                Log.e("FirebaseError", "Failed: ${err.message}")
                Toast.makeText(this, "Failed: ${err.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

