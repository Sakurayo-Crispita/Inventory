package com.cristopher.inventariopersonalapp.ui.inventory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cristopher.inventariopersonalapp.R
import com.cristopher.inventariopersonalapp.data.model.Item
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import com.cristopher.inventariopersonalapp.data.repository.ItemRepository
import com.cristopher.inventariopersonalapp.databinding.ActivityItemAddEditBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class ItemAddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemAddEditBinding
    private lateinit var addEditItemViewModel: ItemAddEditViewModel
    private var itemId: String? = null
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemId = intent.getStringExtra("ITEM_ID")
        if (itemId != null) {
            binding.tvTitle.text = "Editar Ítem"
            binding.etNombre.setText(intent.getStringExtra("ITEM_NOMBRE"))
            binding.etCategoria.setText(intent.getStringExtra("ITEM_CATEGORIA"))
            binding.etCantidad.setText(intent.getIntExtra("ITEM_CANTIDAD", 0).toString())
            binding.etImagenUrl.setText(intent.getStringExtra("ITEM_IMAGEN_URL"))
        } else {
            binding.tvTitle.text = "Añadir Nuevo Ítem"
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        val authRepository = AuthRepository(firebaseAuth, firestore)
        val itemRepository = ItemRepository(firestore)

        addEditItemViewModel = ViewModelProvider(
            this,
            AddEditItemViewModelFactory(itemRepository, authRepository)
        ).get(ItemAddEditViewModel::class.java)

        itemId = intent.getStringExtra("ITEM_ID")
        isEditing = itemId != null

        if (isEditing) {
            binding.tvTitle.text = "Editar Ítem"
            binding.etNombre.setText(intent.getStringExtra("ITEM_NOMBRE"))
            binding.etCategoria.setText(intent.getStringExtra("ITEM_CATEGORIA"))
            binding.etCantidad.setText(intent.getIntExtra("ITEM_CANTIDAD", 0).toString())
            binding.etImagenUrl.setText(intent.getStringExtra("ITEM_IMAGEN_URL"))
        } else {
            binding.tvTitle.text = "Añadir Nuevo Ítem"
        }

        addEditItemViewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, if (isEditing) "Ítem actualizado" else "Ítem guardado", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLoadPreview.setOnClickListener {
            val url = binding.etImagenUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                binding.ivPreviewImage.visibility = View.VISIBLE
                Glide.with(this)
                    .load(url)
                    .into(binding.ivPreviewImage)
            } else {
                Toast.makeText(this, "Introduce una URL válida", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSaveItem.setOnClickListener {
            saveItem()
        }
    }

    private fun saveItem() {
        val nombre = binding.etNombre.text.toString().trim()
        val categoria = binding.etCategoria.text.toString().trim()
        val cantidadString = binding.etCantidad.text.toString().trim()
        val imagenUrl = binding.etImagenUrl.text.toString().trim()

        if (nombre.isEmpty() || categoria.isEmpty() || cantidadString.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = cantidadString.toIntOrNull()
        if (cantidad == null || cantidad < 0) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val item = Item(
            id = itemId ?: UUID.randomUUID().toString(),
            nombre = nombre,
            categoria = categoria,
            cantidad = cantidad,
            imagenUrl = imagenUrl,
            ownerId = ""
        )

        addEditItemViewModel.saveItem(item)
    }
}


class AddEditItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemAddEditViewModel(itemRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
