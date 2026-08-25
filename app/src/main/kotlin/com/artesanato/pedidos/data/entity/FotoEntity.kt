package com.artesanato.pedidos.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fotos",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val caminhoFoto: String,
    val descricao: String = ""
)