package com.artesanato.pedidos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.artesanato.pedidos.data.entity.FotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoDao {
    @Insert
    suspend fun inserir(foto: FotoEntity): Long

    @Delete
    suspend fun deletar(foto: FotoEntity)

    @Query("SELECT * FROM fotos WHERE pedidoId = :pedidoId")
    fun obterPorPedido(pedidoId: Int): Flow<List<FotoEntity>>

    @Query("SELECT * FROM fotos WHERE id = :fotoId")
    fun obterPorId(fotoId: Int): Flow<FotoEntity?>
}