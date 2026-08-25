package com.artesanato.pedidos.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.artesanato.pedidos.data.dao.PedidoDao
import com.artesanato.pedidos.data.dao.EtapaDao
import com.artesanato.pedidos.data.dao.FotoDao
import com.artesanato.pedidos.data.entity.PedidoEntity
import com.artesanato.pedidos.data.entity.EtapaEntity
import com.artesanato.pedidos.data.entity.FotoEntity
import com.artesanato.pedidos.data.converter.DateConverter

@Database(
    entities = [PedidoEntity::class, EtapaEntity::class, FotoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class PedidoDatabase : RoomDatabase() {
    abstract fun pedidoDao(): PedidoDao
    abstract fun etapaDao(): EtapaDao
    abstract fun fotoDao(): FotoDao

    companion object {
        @Volatile
        private var Instance: PedidoDatabase? = null

        fun getDatabase(context: Context): PedidoDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PedidoDatabase::class.java,
                    "pedidos_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}