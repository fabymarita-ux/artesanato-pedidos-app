package com.artesanato.pedidos.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cliente: String,
    val descricao: String,
    val categoria: String,
    val dataPedido: LocalDateTime,
    val dataEntrega: LocalDateTime,
    val valor: Double,
    val status: String = "Não iniciado",
    val criadoEm: LocalDateTime = LocalDateTime.now()
)