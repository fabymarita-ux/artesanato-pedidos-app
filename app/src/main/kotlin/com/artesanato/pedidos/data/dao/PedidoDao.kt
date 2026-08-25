package com.artesanato.pedidos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.artesanato.pedidos.data.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Insert
    suspend fun inserir(pedido: PedidoEntity): Long

    @Update
    suspend fun atualizar(pedido: PedidoEntity)

    @Delete
    suspend fun deletar(pedido: PedidoEntity)

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    fun obterPorId(pedidoId: Int): Flow<PedidoEntity?>

    @Query("SELECT * FROM pedidos ORDER BY dataEntrega ASC")
    fun obterTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE categoria = :categoria ORDER BY dataEntrega ASC")
    fun obterPorCategoria(categoria: String): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE status = :status ORDER BY dataEntrega ASC")
    fun obterPorStatus(status: String): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE cliente LIKE :termo ORDER BY dataEntrega ASC")
    fun pesquisar(termo: String): Flow<List<PedidoEntity>>
}