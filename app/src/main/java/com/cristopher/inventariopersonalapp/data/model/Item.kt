package com.cristopher.inventariopersonalapp.data.model

import com.google.firebase.firestore.DocumentId

    data class Item(
        @DocumentId
        var id: String = "",
        var nombre: String = "",
        var categoria: String = "",
        var cantidad: Int = 0,
        var imagenUrl: String = "",
        var ownerId: String = ""
    )