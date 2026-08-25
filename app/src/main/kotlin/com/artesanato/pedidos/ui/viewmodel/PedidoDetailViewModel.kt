package com.artesanato.pedidos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artesanato.pedidos.data.entity.EtapaEntity
import com.artesanato.pedidos.data.entity.FotoEntity
import com.artesanato.pedidos.data.entity.PedidoEntity
import com.artesanato.pedidos.data.repository.PedidoRepository
import com.artesanato.pedidos.ui.model.UiEtapa
import com.artesanato.pedidos.ui.model.UiFoto
import com.artesanato.pedidos.ui.model.UiPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class PedidoDetailState(
    val pedido: UiPedido? = null,
    val etapas: List<UiEtapa> = emptyList(),
    val fotos: List<UiFoto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PedidoDetailViewModel(private val repository: PedidoRepository) : ViewModel() {
    private val _state = MutableStateFlow(PedidoDetailState())
    val state: StateFlow<PedidoDetailState> = _state.asStateFlow()

    fun carregarDetalhes(pedidoId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.obterPedidoPorId(pedidoId).collect { pedido ->
                    if (pedido != null) {
                        val uiPedido = convertToUi(pedido)
                        _state.update { it.copy(pedido = uiPedido) }
                    }
                }
                repository.obterEtapasPorPedido(pedidoId).collect { etapas ->
                    val uiEtapas = etapas.map { UiEtapa(
                        id = it.id,
                        pedidoId = it.pedidoId,
                        titulo = it.titulo,
                        descricao = it.descricao,
                        concluida = it.concluida,
                        ordem = it.ordem
                    ) }
                    _state.update { it.copy(etapas = uiEtapas) }
                }
                repository.obterFotosPorPedido(pedidoId).collect { fotos ->
                    val uiFotos = fotos.map { UiFoto(
                        id = it.id,
                        pedidoId = it.pedidoId,
                        caminhoFoto = it.caminhoFoto,
                        descricao = it.descricao
                    ) }
                    _state.update { it.copy(fotos = uiFotos, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun toggleEtapa(etapa: UiEtapa) {
        viewModelScope.launch {
            val entity = EtapaEntity(
                id = etapa.id,
                pedidoId = etapa.pedidoId,
                titulo = etapa.titulo,
                descricao = etapa.descricao,
                concluida = !etapa.concluida,
                ordem = etapa.ordem
            )
            repository.atualizarEtapa(entity)
        }
    }

    fun adicionarEtapa(titulo: String, descricao: String, pedidoId: Int, ordem: Int) {
        viewModelScope.launch {
            val etapa = EtapaEntity(
                pedidoId = pedidoId,
                titulo = titulo,
                descricao = descricao,
                concluida = false,
                ordem = ordem
            )
            repository.inserirEtapa(etapa)
        }
    }

    fun deletarEtapa(etapa: UiEtapa) {
        viewModelScope.launch {
            val entity = EtapaEntity(
                id = etapa.id,
                pedidoId = etapa.pedidoId,
                titulo = etapa.titulo,
                descricao = etapa.descricao,
                concluida = etapa.concluida,
                ordem = etapa.ordem
            )
            repository.deletarEtapa(entity)
        }
    }

    fun adicionarFoto(caminhoFoto: String, descricao: String, pedidoId: Int) {
        viewModelScope.launch {
            val foto = FotoEntity(
                pedidoId = pedidoId,
                caminhoFoto = caminhoFoto,
                descricao = descricao
            )
            repository.inserirFoto(foto)
        }
    }

    fun deletarFoto(foto: UiFoto) {
        viewModelScope.launch {
            val entity = FotoEntity(
                id = foto.id,
                pedidoId = foto.pedidoId,
                caminhoFoto = foto.caminhoFoto,
                descricao = foto.descricao
            )
            repository.deletarFoto(entity)
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