package com.farmmanager.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "crops")
data class CropEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val variety: String,
    val fieldName: String,
    val area: Double,
    val season: String,
    val sowingDate: String,
    val notes: String = "",
)

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cropId")],
)
data class ExpenseEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: Long,
    val category: String,
    val applicationRound: Int?,
    val amount: Double,
    val expenseDate: String,
    val notes: String = "",
)

@Entity(
    tableName = "harvests",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cropId")],
)
data class HarvestEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: Long,
    val harvestDate: String,
    val quantityKg: Double,
    val managementNotes: String = "",
)

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cropId")],
)
data class SaleEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropId: Long,
    val saleDate: String,
    val quantityKg: Double,
    val pricePerKg: Double,
    val buyerName: String,
    val buyerPhone: String = "",
    val notes: String = "",
) {
    val totalIncome: Double get() = quantityKg * pricePerKg
}

data class CropSummary(
    val cropId: Long,
    val cropName: String,
    val totalExpenses: Double,
    val totalIncome: Double,
    val harvestedKg: Double,
) {
    val profitLoss: Double get() = totalIncome - totalExpenses
}

data class FarmSnapshot(
    val crops: List<CropEntity> = emptyList(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val harvests: List<HarvestEntity> = emptyList(),
    val sales: List<SaleEntity> = emptyList(),
)

@Dao
interface FarmDao {
    @Query("SELECT * FROM crops ORDER BY sowingDate DESC, id DESC")
    fun observeCrops(): Flow<List<CropEntity>>

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC, id DESC")
    fun observeExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM harvests ORDER BY harvestDate DESC, id DESC")
    fun observeHarvests(): Flow<List<HarvestEntity>>

    @Query("SELECT * FROM sales ORDER BY saleDate DESC, id DESC")
    fun observeSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM crops ORDER BY id")
    suspend fun getCrops(): List<CropEntity>

    @Query("SELECT * FROM expenses ORDER BY id")
    suspend fun getExpenses(): List<ExpenseEntity>

    @Query("SELECT * FROM harvests ORDER BY id")
    suspend fun getHarvests(): List<HarvestEntity>

    @Query("SELECT * FROM sales ORDER BY id")
    suspend fun getSales(): List<SaleEntity>

    @Query("SELECT * FROM crops WHERE id = :id LIMIT 1")
    suspend fun getCropById(id: Long): CropEntity?

    @Query(
        """
        SELECT * FROM crops
        WHERE name = :name
        AND variety = :variety
        AND fieldName = :fieldName
        AND sowingDate = :sowingDate
        LIMIT 1
        """,
    )
    suspend fun findCrop(name: String, variety: String, fieldName: String, sowingDate: String): CropEntity?

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query(
        """
        SELECT * FROM expenses
        WHERE cropId = :cropId
        AND category = :category
        AND ((applicationRound IS NULL AND :applicationRound IS NULL) OR applicationRound = :applicationRound)
        AND expenseDate = :expenseDate
        AND notes = :notes
        LIMIT 1
        """,
    )
    suspend fun findExpense(
        cropId: Long,
        category: String,
        applicationRound: Int?,
        expenseDate: String,
        notes: String,
    ): ExpenseEntity?

    @Query("SELECT * FROM harvests WHERE id = :id LIMIT 1")
    suspend fun getHarvestById(id: Long): HarvestEntity?

    @Query(
        """
        SELECT * FROM harvests
        WHERE cropId = :cropId
        AND harvestDate = :harvestDate
        AND managementNotes = :managementNotes
        LIMIT 1
        """,
    )
    suspend fun findHarvest(cropId: Long, harvestDate: String, managementNotes: String): HarvestEntity?

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): SaleEntity?

    @Query(
        """
        SELECT * FROM sales
        WHERE cropId = :cropId
        AND saleDate = :saleDate
        AND buyerName = :buyerName
        AND buyerPhone = :buyerPhone
        AND notes = :notes
        LIMIT 1
        """,
    )
    suspend fun findSale(
        cropId: Long,
        saleDate: String,
        buyerName: String,
        buyerPhone: String,
        notes: String,
    ): SaleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrop(crop: CropEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHarvest(harvest: HarvestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSale(sale: SaleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrops(crops: List<CropEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpenses(expenses: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHarvests(harvests: List<HarvestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSales(sales: List<SaleEntity>)

    @Query("DELETE FROM sales")
    suspend fun deleteSales()

    @Query("DELETE FROM harvests")
    suspend fun deleteHarvests()

    @Query("DELETE FROM expenses")
    suspend fun deleteExpenses()

    @Query("DELETE FROM crops")
    suspend fun deleteCrops()

    @Transaction
    suspend fun replaceAll(snapshot: FarmSnapshot) {
        deleteSales()
        deleteHarvests()
        deleteExpenses()
        deleteCrops()
        upsertCrops(snapshot.crops)
        upsertExpenses(snapshot.expenses)
        upsertHarvests(snapshot.harvests)
        upsertSales(snapshot.sales)
    }

    @Transaction
    suspend fun mergeImport(snapshot: FarmSnapshot) {
        val cropIdMap = mutableMapOf<Long, Long>()

        snapshot.crops.forEach { importedCrop ->
            val existingCrop = findCrop(
                name = importedCrop.name,
                variety = importedCrop.variety,
                fieldName = importedCrop.fieldName,
                sowingDate = importedCrop.sowingDate,
            ) ?: importedCrop.id.takeIf { it > 0 }?.let { getCropById(it) }

            val cropToSave = existingCrop?.let { importedCrop.copy(id = it.id) } ?: importedCrop
            val savedId = upsertCrop(cropToSave)
            cropIdMap[importedCrop.id] = cropToSave.id.takeIf { it > 0 } ?: savedId
        }

        snapshot.expenses.forEach { importedExpense ->
            val cropId = cropIdMap[importedExpense.cropId] ?: importedExpense.cropId
            val remappedExpense = importedExpense.copy(cropId = cropId)
            val existingExpense = findExpense(
                cropId = cropId,
                category = remappedExpense.category,
                applicationRound = remappedExpense.applicationRound,
                expenseDate = remappedExpense.expenseDate,
                notes = remappedExpense.notes,
            ) ?: remappedExpense.id.takeIf { it > 0 }?.let { getExpenseById(it) }

            upsertExpense(existingExpense?.let { remappedExpense.copy(id = it.id) } ?: remappedExpense)
        }

        snapshot.harvests.forEach { importedHarvest ->
            val cropId = cropIdMap[importedHarvest.cropId] ?: importedHarvest.cropId
            val remappedHarvest = importedHarvest.copy(cropId = cropId)
            val existingHarvest = findHarvest(
                cropId = cropId,
                harvestDate = remappedHarvest.harvestDate,
                managementNotes = remappedHarvest.managementNotes,
            ) ?: remappedHarvest.id.takeIf { it > 0 }?.let { getHarvestById(it) }

            upsertHarvest(existingHarvest?.let { remappedHarvest.copy(id = it.id) } ?: remappedHarvest)
        }

        snapshot.sales.forEach { importedSale ->
            val cropId = cropIdMap[importedSale.cropId] ?: importedSale.cropId
            val remappedSale = importedSale.copy(cropId = cropId)
            val existingSale = findSale(
                cropId = cropId,
                saleDate = remappedSale.saleDate,
                buyerName = remappedSale.buyerName,
                buyerPhone = remappedSale.buyerPhone,
                notes = remappedSale.notes,
            ) ?: remappedSale.id.takeIf { it > 0 }?.let { getSaleById(it) }

            upsertSale(existingSale?.let { remappedSale.copy(id = it.id) } ?: remappedSale)
        }
    }
}

@Database(
    entities = [CropEntity::class, ExpenseEntity::class, HarvestEntity::class, SaleEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FarmDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao

    companion object {
        @Volatile private var instance: FarmDatabase? = null

        fun get(context: Context): FarmDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FarmDatabase::class.java,
                    "farm_manager.db",
                ).build().also { instance = it }
            }
    }
}
