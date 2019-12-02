package com.bugrakandemir.instaclonefb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var userEmailFromFirebase: ArrayList<String> = ArrayList()
    var userCommentFromFirebase: ArrayList<String> = ArrayList()
    var userImageFromFirebase: ArrayList<String> = ArrayList()

    var adapter: FeedRecyclerAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_post) {
            val intent = Intent(applicationContext, UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FeedRecyclerAdapter(
            userEmailFromFirebase,
            userCommentFromFirebase,
            userImageFromFirebase
        )
        recyclerView.adapter = adapter
    }

    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            userEmailFromFirebase.clear()
                            userCommentFromFirebase.clear()
                            userImageFromFirebase.clear()

                            val documents = snapshot.documents
                            for (document in documents) {
                                val comment = document.get("comment") as String
                                val useremail = document.get("userEmail") as String
                                val downloadURL = document.get("downloadURL") as String
                                val timeStamp = document.get("date") as Timestamp
                                val date = timeStamp.toDate()

                                userEmailFromFirebase.add(useremail)
                                userCommentFromFirebase.add(comment)
                                userImageFromFirebase.add(downloadURL)

                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
    }
}
