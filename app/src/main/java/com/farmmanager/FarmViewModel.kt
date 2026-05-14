package com.farmmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmmanager.data.FarmRepository
import com.farmmanager.export.ExcelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FarmViewModel @Inject constructor(
    private val repository: FarmRepository,
    private val excelManager: ExcelManager,
) : ViewModel() {
    val uiState = combine(
        repository.crops,
        repository.expenses,
        repository.harvests,
        repository.sales,
        repository.summaries,
    ) { crops, expenses, harvests, sales, summaries ->
        FarmUiState(crops, expenses, harvests, sales, summaries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FarmUiState())

    var statusMessage by mutableStateOf<String?>(null)
        private set

    fun onIntent(intent: FarmIntent) {
        when (intent) {
            FarmIntent.ClearStatus -> statusMessage = null
            is FarmIntent.AddCrop -> launch("Crop saved") { repository.addCrop(intent.crop) }
            is FarmIntent.AddExpense -> launch("Expense saved") { repository.addExpense(intent.expense) }
            is FarmIntent.AddHarvest -> launch("Harvest saved") { repository.addHarvest(intent.harvest) }
            is FarmIntent.AddSale -> launch("Sale saved") { repository.addSale(intent.sale) }
            is FarmIntent.ExportAll -> exportTo(intent.context, intent.uri)
            is FarmIntent.ImportAll -> importFrom(intent.context, intent.uri)
            is FarmIntent.ExportSection -> exportSectionTo(intent.context, intent.uri, intent.section)
            is FarmIntent.ImportSection -> importSectionFrom(intent.context, intent.uri, intent.section)
            is FarmIntent.ShareExcel -> shareExcel(intent.context)
        }
    }

    private fun exportTo(context: Context, uri: Uri) = viewModelScope.launch {
        runCatching {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                excelManager.export(repository.snapshot(), output)
            } ?: error("Unable to open export file")
        }.onSuccess {
            statusMessage = "Farm data exported to Excel"
        }.onFailure {
            statusMessage = "Export failed: ${it.message}"
        }
    }

    private fun exportSectionTo(context: Context, uri: Uri, section: DataSection) = viewModelScope.launch {
        runCatching {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                excelManager.export(repository.snapshot().onlySection(section), output)
            } ?: error("Unable to open export file")
        }.onSuccess {
            statusMessage = "${section.label} data exported to Excel"
        }.onFailure {
            statusMessage = "Export failed: ${it.message}"
        }
    }

    private fun importFrom(context: Context, uri: Uri) = viewModelScope.launch {
        runCatching {
            val snapshot = context.contentResolver.openInputStream(uri)?.use { input ->
                excelManager.import(input)
            } ?: error("Unable to open import file")
            repository.mergeImport(snapshot)
        }.onSuccess {
            statusMessage = "Farm data imported without duplicates"
        }.onFailure {
            statusMessage = "Import failed: ${it.message}"
        }
    }

    private fun importSectionFrom(context: Context, uri: Uri, section: DataSection) = viewModelScope.launch {
        runCatching {
            val snapshot = context.contentResolver.openInputStream(uri)?.use { input ->
                excelManager.import(input)
            } ?: error("Unable to open import file")
            repository.mergeImport(snapshot.onlySection(section))
        }.onSuccess {
            statusMessage = "${section.label} data imported without duplicates"
        }.onFailure {
            statusMessage = "Import failed: ${it.message}"
        }
    }

    private fun shareExcel(context: Context) = viewModelScope.launch {
        runCatching {
            val exportDir = File(context.cacheDir, "exports").also { it.mkdirs() }
            val file = File(exportDir, "Farm_Manager_Data_${exportTimestamp()}.xlsx")
            file.outputStream().use { output -> excelManager.export(repository.snapshot(), output) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share farm Excel file"))
        }.onFailure {
            statusMessage = "Share failed: ${it.message}"
        }
    }

    private fun launch(success: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { statusMessage = success }
                .onFailure { statusMessage = "Save failed: ${it.message}" }
        }
    }
}
