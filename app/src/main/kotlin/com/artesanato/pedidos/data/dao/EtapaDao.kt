package com.artesanato.pedidos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.artesanato.pedidos.data.entity.EtapaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EtapaDao {
    @Insert
    suspend fun inserir(etapa: EtapaEntity): Long

    @Update
    suspend fun atualizar(etapa: EtapaEntity)

    @Delete
    suspend fun deletar(etapa: EtapaEntity)

    @Query("SELECT * FROM etapas WHERE pedidoId = :pedidoId ORDER BY ordem ASC")
    fun obterPorPedido(pedidoId: Int): Flow<List<EtapaEntity>>

    @Query("SELECT * FROM etapas WHERE id = :etapaId")
    fun obterPorId(etapaId: Int): Flow<EtapaEntity?>

    @Query("SELECT COUNT(*) FROM etapas WHERE pedidoId = :pedidoId AND concluida = 1")
    fun contarConcluidas(pedidoId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM etapas WHERE pedidoId = :pedidoId")
    fun contarTotal(pedidoId: Int): Flow<Int>
}