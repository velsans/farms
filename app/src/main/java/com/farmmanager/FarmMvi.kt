package com.farmmanager

import android.content.Context
import android.net.Uri
import com.farmmanager.data.CropEntity
import com.farmmanager.data.ExpenseEntity
import com.farmmanager.data.HarvestEntity
import com.farmmanager.data.SaleEntity

sealed interface FarmIntent {
    data object ClearStatus : FarmIntent
    data class AddCrop(val crop: CropEntity) : FarmIntent
    data class AddExpense(val expense: ExpenseEntity) : FarmIntent
    data class AddHarvest(val harvest: HarvestEntity) : FarmIntent
    data class AddSale(val sale: SaleEntity) : FarmIntent
    data class ExportAll(val context: Context, val uri: Uri) : FarmIntent
    data class ImportAll(val context: Context, val uri: Uri) : FarmIntent
    data class ExportSection(val context: Context, val uri: Uri, val section: DataSection) : FarmIntent
    data class ImportSection(val context: Context, val uri: Uri, val section: DataSection) : FarmIntent
    data class ShareExcel(val context: Context) : FarmIntent
}

sealed interface FarmEffect {
    data class Message(val text: String) : FarmEffect
}
