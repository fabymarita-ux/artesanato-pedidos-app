package com.artesanato.pedidos.ui.model

import java.time.LocalDateTime

data class UiPedido(
    val id: Int,
    val cliente: String,
    val descricao: String,
    val categoria: String,
    val dataPedido: LocalDateTime,
    val dataEntrega: LocalDateTime,
    val valor: Double,
    val status: String,
    val progresso: Float = 0f
)

data class UiEtapa(
    val id: Int,
    val pedidoId: Int,
    val titulo: String,
    val descricao: String,
    val concluida: Boolean,
    val ordem: Int
)

data class UiFoto(
    val id: Int,
    val pedidoId: Int,
    val caminhoFoto: String,
    val descricao: String
)