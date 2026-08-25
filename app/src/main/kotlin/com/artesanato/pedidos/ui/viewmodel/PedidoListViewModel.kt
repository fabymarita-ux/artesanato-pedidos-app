package com.artesanato.pedidos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artesanato.pedidos.data.entity.EtapaEntity
import com.artesanato.pedidos.data.entity.PedidoEntity
import com.artesanato.pedidos.data.repository.PedidoRepository
import com.artesanato.pedidos.ui.model.UiPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class PedidoListState(
    val pedidos: List<UiPedido> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filtroCategoria: String? = null,
    val filtroStatus: String? = null
)

class PedidoListViewModel(private val repository: PedidoRepository) : ViewModel() {
    private val _state = MutableStateFlow(PedidoListState())
    val state: StateFlow<PedidoListState> = _state.asStateFlow()

    init {
        carregarPedidos()
    }

    fun carregarPedidos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.obterTodosPedidos().collect { pedidos ->
                    val uiPedidos = pedidos.map { convertToUi(it) }.sortedBy { it.dataEntrega }
                    _state.update { it.copy(pedidos = uiPedidos, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun filtrarPorCategoria(categoria: String?) {
        viewModelScope.launch {
            _state.update { it.copy(filtroCategoria = categoria) }
            if (categoria != null) {
                repository.obterPedidosPorCategoria(categoria).collect { pedidos ->
                    val uiPedidos = pedidos.map { convertToUi(it) }.sortedBy { it.dataEntrega }
                    _state.update { it.copy(pedidos = uiPedidos) }
                }
            } else {
                carregarPedidos()
            }
        }
    }

    fun filtrarPorStatus(status: String?) {
        viewModelScope.launch {
            _state.update { it.copy(filtroStatus = status) }
            if (status != null) {
                repository.obterPedidosPorStatus(status).collect { pedidos ->
                    val uiPedidos = pedidos.map { convertToUi(it) }.sortedBy { it.dataEntrega }
                    _state.update { it.copy(pedidos = uiPedidos) }
                }
            } else {
                carregarPedidos()
            }
        }
    }

    fun pesquisar(termo: String) {
        viewModelScope.launch {
            if (termo.isBlank()) {
                carregarPedidos()
            } else {
                repository.pesquisarPedidos(termo).collect { pedidos ->
                    val uiPedidos = pedidos.map { convertToUi(it) }.sortedBy { it.dataEntrega }
                    _state.update { it.copy(pedidos = uiPedidos) }
                }
            }
        }
    }

    fun deletarPedido(pedido: UiPedido) {
        viewModelScope.launch {
            val entity = PedidoEntity(
                id = pedido.id,
                cliente = pedido.cliente,
                descricao = pedido.descricao,
                categoria = pedido.categoria,
                dataPedido = pedido.dataPedido,
                dataEntrega = pedido.dataEntrega,
                valor = pedido.valor,
                status = pedido.status
            )
            repository.deletarPedido(entity)
            carregarPedidos()
        }
    }

    private suspend fun convertToUi(pedido: PedidoEntity): UiPedido {
        val (concluidas, total) = repository.contarEtapasConcluidasETotal(pedido.id)
            .collect { Pair(0, 0) }
        val progresso = if (total > 0) concluidas.toFloat() / total else 0f
        return UiPedido(
            id = pedido.id,
            cliente = pedido.cliente,
            descricao = pedido.descricao,
            categoria = pedido.categoria,
            dataPedido = pedido.dataPedido,
            dataEntrega = pedido.dataEntrega,
            valor = pedido.valor,
            status = pedido.status,
            progresso = progresso
        )
    }
}