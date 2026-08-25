package com.artesanato.pedidos.data.repository

import com.artesanato.pedidos.data.dao.PedidoDao
import com.artesanato.pedidos.data.dao.EtapaDao
import com.artesanato.pedidos.data.dao.FotoDao
import com.artesanato.pedidos.data.entity.EtapaEntity
import com.artesanato.pedidos.data.entity.FotoEntity
import com.artesanato.pedidos.data.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val etapaDao: EtapaDao,
    private val fotoDao: FotoDao
) {
    // Pedidos
    fun obterTodosPedidos(): Flow<List<PedidoEntity>> = pedidoDao.obterTodos()
    fun obterPedidoPorId(id: Int): Flow<PedidoEntity?> = pedidoDao.obterPorId(id)
    fun obterPedidosPorCategoria(categoria: String): Flow<List<PedidoEntity>> =
        pedidoDao.obterPorCategoria(categoria)
    fun obterPedidosPorStatus(status: String): Flow<List<PedidoEntity>> =
        pedidoDao.obterPorStatus(status)
    fun pesquisarPedidos(termo: String): Flow<List<PedidoEntity>> =
        pedidoDao.pesquisar("%$termo%")

    suspend fun inserirPedido(pedido: PedidoEntity): Long = pedidoDao.inserir(pedido)
    suspend fun atualizarPedido(pedido: PedidoEntity) = pedidoDao.atualizar(pedido)
    suspend fun deletarPedido(pedido: PedidoEntity) = pedidoDao.deletar(pedido)

    // Etapas
    fun obterEtapasPorPedido(pedidoId: Int): Flow<List<EtapaEntity>> =
        etapaDao.obterPorPedido(pedidoId)
    fun obterEtapaPorId(id: Int): Flow<EtapaEntity?> = etapaDao.obterPorId(id)

    suspend fun inserirEtapa(etapa: EtapaEntity): Long = etapaDao.inserir(etapa)
    suspend fun atualizarEtapa(etapa: EtapaEntity) = etapaDao.atualizar(etapa)
    suspend fun deletarEtapa(etapa: EtapaEntity) = etapaDao.deletar(etapa)

    fun contarEtapasConcluidasETotal(pedidoId: Int): Flow<Pair<Int, Int>> {
        return kotlinx.coroutines.flow.combine(
            etapaDao.contarConcluidas(pedidoId),
            etapaDao.contarTotal(pedidoId)
        ) { concluidas, total -> Pair(concluidas, total) }
    }

    // Fotos
    fun obterFotosPorPedido(pedidoId: Int): Flow<List<FotoEntity>> =
        fotoDao.obterPorPedido(pedidoId)
    fun obterFotoPorId(id: Int): Flow<FotoEntity?> = fotoDao.obterPorId(id)

    suspend fun inserirFoto(foto: FotoEntity): Long = fotoDao.inserir(foto)
    suspend fun deletarFoto(foto: FotoEntity) = fotoDao.deletar(foto)
}

fun kotlinx.coroutines.flow.Flow.Companion.combine(
    flow1: Flow<Int>,
    flow2: Flow<Int>,
    transform: suspend (Int, Int) -> Pair<Int, Int>
): Flow<Pair<Int, Int>> {
    return kotlinx.coroutines.flow.combine(flow1, flow2, transform)
}