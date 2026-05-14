package com.farmmanager.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class FarmRepository(private val dao: FarmDao) {
    val crops: Flow<List<CropEntity>> = dao.observeCrops()
    val expenses: Flow<List<ExpenseEntity>> = dao.observeExpenses()
    val harvests: Flow<List<HarvestEntity>> = dao.observeHarvests()
    val sales: Flow<List<SaleEntity>> = dao.observeSales()

    val summaries: Flow<List<CropSummary>> = combine(crops, expenses, harvests, sales) { cropList, expenseList, harvestList, saleList ->
        cropList.map { crop ->
            CropSummary(
                cropId = crop.id,
                cropName = crop.name,
                totalExpenses = expenseList.filter { it.cropId == crop.id }.sumOf { it.amount },
                totalIncome = saleList.filter { it.cropId == crop.id }.sumOf { it.totalIncome },
                harvestedKg = harvestList.filter { it.cropId == crop.id }.sumOf { it.quantityKg },
            )
        }
    }

    suspend fun snapshot(): FarmSnapshot = FarmSnapshot(
        crops = dao.getCrops(),
        expenses = dao.getExpenses(),
        harvests = dao.getHarvests(),
        sales = dao.getSales(),
    )

    suspend fun mergeImport(snapshot: FarmSnapshot) = dao.mergeImport(snapshot)

    suspend fun addCrop(crop: CropEntity) = dao.upsertCrop(crop)

    suspend fun addExpense(expense: ExpenseEntity) = dao.upsertExpense(expense)

    suspend fun addHarvest(harvest: HarvestEntity) = dao.upsertHarvest(harvest)

    suspend fun addSale(sale: SaleEntity) = dao.upsertSale(sale)
}
