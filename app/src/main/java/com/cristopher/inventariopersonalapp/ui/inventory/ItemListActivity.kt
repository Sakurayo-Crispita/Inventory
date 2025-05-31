package com.cristopher.inventariopersonalapp.ui.inventory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cristopher.inventariopersonalapp.R
import com.cristopher.inventariopersonalapp.data.model.Item
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import com.cristopher.inventariopersonalapp.data.repository.ItemRepository
import com.cristopher.inventariopersonalapp.databinding.ActivityItemListBinding
import com.cristopher.inventariopersonalapp.ui.auth.LoginActivity
import com.cristopher.inventariopersonalapp.ui.inventory.adapter.ItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItemListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemListBinding
    private lateinit var adapter: ItemAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ItemAdapter(
            onEditClick = { item ->
                val intent = Intent(this, ItemAddEditActivity::class.java).apply {
                    putExtra("ITEM_ID", item.id)
                    putExtra("ITEM_NOMBRE", item.nombre)
                    putExtra("ITEM_CATEGORIA", item.categoria)
                    putExtra("ITEM_CANTIDAD", item.cantidad)
                    putExtra("ITEM_IMAGEN_URL", item.imagenUrl)
                }
                startActivity(intent)
            },
            onDeleteClick = { itemId ->
                eliminarItem(itemId)
            }
        )

        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)

        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, ItemAddEditActivity::class.java)
            startActivity(intent)
        }

        binding.btnCerrarSesion.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun loadItems() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            navigateToLogin()
            return
        }

        firestore.collection("item")
            .whereEqualTo("ownerId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.toObjects(Item::class.java)
                adapter.submitList(items)

                if (items.isEmpty()) {
                    Toast.makeText(this, "No tienes items registrados", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar items: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarItem(itemId: String) {
        firestore.collection("item")
            .document(itemId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item eliminado", Toast.LENGTH_SHORT).show()
                loadItems()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


