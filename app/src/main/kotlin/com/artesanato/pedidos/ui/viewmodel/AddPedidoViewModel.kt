package com.artesanato.pedidos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artesanato.pedidos.data.entity.PedidoEntity
import com.artesanato.pedidos.data.repository.PedidoRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AddPedidoViewModel(private val repository: PedidoRepository) : ViewModel() {
    fun salvarPedido(
        cliente: String,
        descricao: String,
        categoria: String,
        dataEntrega: LocalDateTime,
        valor: Double
    ) {
        viewModelScope.launch {
            val pedido = PedidoEntity(
                cliente = cliente,
                descricao = descricao,
                categoria = categoria,
                dataPedido = LocalDateTime.now(),
                dataEntrega = dataEntrega,
                valor = valor,
                status = "Não iniciado"
            )
            repository.inserirPedido(pedido)
        }
    }
}