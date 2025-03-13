package activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.bookapp.Book
import com.example.bookapp.R
import com.google.firebase.database.FirebaseDatabase

class BookDetailsFragment : DialogFragment() {

    private lateinit var etBookTitle: EditText
    private lateinit var etBookAuthor: EditText
    private lateinit var btnUpdateSave: Button
    private lateinit var deleteButton: Button
    private var isEditing = false
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getString("bookId")  
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etBookTitle = view.findViewById(R.id.etBookTitle)
        etBookAuthor = view.findViewById(R.id.etBookAuthor)
        btnUpdateSave = view.findViewById(R.id.updateSaveButton)
        deleteButton = view.findViewById(R.id.deleteButton)


        deleteButton.setOnClickListener {
            deleteBookFromFirebase()
        }


        val bookTitle = arguments?.getString("bookTitle") ?: "No Title"
        val bookAuthor = arguments?.getString("bookAuthor") ?: "No Author"


        etBookTitle.setText(bookTitle)
        etBookAuthor.setText(bookAuthor)

        btnUpdateSave.setOnClickListener {
            if (!isEditing) {
                etBookTitle.isEnabled = true
                etBookAuthor.isEnabled = true
                btnUpdateSave.text = "Save"
                isEditing = true
            } else {
                saveChangesToFirebase()
            }
        }
    }

    private fun deleteBookFromFirebase() {
        if (!bookId.isNullOrEmpty()) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Books").child(bookId!!)

            dbRef.removeValue().addOnSuccessListener {
                Toast.makeText(requireContext(), "Book deleted successfully", Toast.LENGTH_SHORT).show()
                dismiss() 
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete book", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChangesToFirebase() {
        val updatedTitle = etBookTitle.text.toString()
        val updatedAuthor = etBookAuthor.text.toString()

        if (!bookId.isNullOrEmpty()) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Books").child(bookId!!)
            val updatedBook = mapOf(
                "bookName" to updatedTitle,
                "authorName" to updatedAuthor
            )
            dbRef.updateChildren(updatedBook).addOnSuccessListener {
                etBookTitle.isEnabled = false
                etBookAuthor.isEnabled = false
                btnUpdateSave.text = "Update"
                isEditing = false
                Toast.makeText(requireContext(), "Book updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(book: Book, bookId: String): BookDetailsFragment {
            val fragment = BookDetailsFragment()
            val args = Bundle().apply {
                putString("bookTitle", book.bookName)
                putString("bookAuthor", book.authorName)
                putString("bookId", bookId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
