package com.farmmanager.ui.agri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.farmmanager.R
import com.farmmanager.DataSection
import com.farmmanager.DetailCard
import com.farmmanager.FarmTab
import com.farmmanager.FarmUiState
import com.farmmanager.HeroCard
import com.farmmanager.ModuleThemeColors
import com.farmmanager.SectionTitle
import com.farmmanager.SectionToolsCard
import com.farmmanager.SectionTotalCard
import com.farmmanager.data.CropEntity
import com.farmmanager.data.ExpenseEntity
import com.farmmanager.data.HarvestEntity
import com.farmmanager.data.SaleEntity
import com.farmmanager.filterByPeriod
import com.farmmanager.formatKg
import com.farmmanager.rupees

@Composable
fun CropsScreen(
    state: FarmUiState,
    selectedMonth: Int?,
    selectedYear: String,
    onMonthSelected: (Int?) -> Unit,
    onYearSelected: (String) -> Unit,
    theme: ModuleThemeColors,
    onImportSection: (DataSection) -> Unit,
    onExportSection: (DataSection) -> Unit,
    onEditCrop: (CropEntity) -> Unit,
) {
    val crops = state.crops.filterByPeriod(selectedMonth, selectedYear) { it.sowingDate }
    val screenPadding = dimensionResource(R.dimen.space_20)
    val itemSpacing = dimensionResource(R.dimen.space_14)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(dimensionResource(R.dimen.grid_min_cell)),
        contentPadding = PaddingValues(start = screenPadding, end = screenPadding, bottom = screenPadding),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionToolsCard(stringResource(R.string.search_crops), selectedMonth, selectedYear, state.availableYears, theme, onMonthSelected, onYearSelected)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTotalCard(
                title = stringResource(R.string.crop_total),
                primaryValue = stringResource(R.string.crop_count, crops.size),
                secondaryValue = stringResource(R.string.area_acres, crops.sumOf { it.area }),
                icon = Icons.Default.Grass,
                tab = FarmTab.Crops,
            )
        }
        items(crops) { crop ->
            val summary = state.summaries.firstOrNull { it.cropId == crop.id }
            DetailCard(
                title = crop.name,
                subtitle = stringResource(R.string.crop_details_format, crop.variety, crop.fieldName, crop.area.toString(), crop.season),
                value = rupees(summary?.profitLoss ?: 0.0),
                onClick = { onEditCrop(crop) },
            )
        }
    }
}

@Composable
fun ExpensesScreen(
    state: FarmUiState,
    selectedMonth: Int?,
    selectedYear: String,
    onMonthSelected: (Int?) -> Unit,
    onYearSelected: (String) -> Unit,
    theme: ModuleThemeColors,
    onImportSection: (DataSection) -> Unit,
    onExportSection: (DataSection) -> Unit,
    onEditExpense: (ExpenseEntity) -> Unit,
) {
    val expenses = state.expenses.filterByPeriod(selectedMonth, selectedYear) { it.expenseDate }
    val screenPadding = dimensionResource(R.dimen.space_20)
    LazyColumn(contentPadding = PaddingValues(start = screenPadding, end = screenPadding, bottom = screenPadding), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12))) {
        item {
            SectionToolsCard(stringResource(R.string.search_expenses), selectedMonth, selectedYear, state.availableYears, theme, onMonthSelected, onYearSelected)
        }
        item {
            SectionTotalCard(
                title = stringResource(R.string.expense_total),
                primaryValue = rupees(expenses.sumOf { it.amount }),
                secondaryValue = stringResource(R.string.entry_count, expenses.size),
                icon = Icons.Default.WaterDrop,
                tab = FarmTab.Expenses,
            )
        }
        item { SectionTitle(stringResource(R.string.expenses_title)) }
        items(expenses) { expense ->
            val crop = state.crops.firstOrNull { it.id == expense.cropId }?.name.orEmpty()
            val round = expense.applicationRound?.let { stringResource(R.string.expense_round_format, it) }.orEmpty()
            DetailCard(
                title = stringResource(R.string.expense_title_format, expense.category, round),
                subtitle = stringResource(R.string.expense_subtitle_format, crop, expense.expenseDate),
                value = rupees(expense.amount),
                onClick = { onEditExpense(expense) },
            )
        }
    }
}

@Composable
fun HarvestScreen(
    state: FarmUiState,
    selectedMonth: Int?,
    selectedYear: String,
    onMonthSelected: (Int?) -> Unit,
    onYearSelected: (String) -> Unit,
    theme: ModuleThemeColors,
    onImportSection: (DataSection) -> Unit,
    onExportSection: (DataSection) -> Unit,
    onEditHarvest: (HarvestEntity) -> Unit,
) {
    val harvests = state.harvests.filterByPeriod(selectedMonth, selectedYear) { it.harvestDate }
    val screenPadding = dimensionResource(R.dimen.space_20)
    LazyColumn(contentPadding = PaddingValues(start = screenPadding, end = screenPadding, bottom = screenPadding), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12))) {
        item {
            SectionToolsCard(stringResource(R.string.search_harvest), selectedMonth, selectedYear, state.availableYears, theme, onMonthSelected, onYearSelected)
        }
        item {
            SectionTotalCard(
                title = stringResource(R.string.harvest_total),
                primaryValue = formatKg(harvests.sumOf { it.quantityKg }),
                secondaryValue = stringResource(R.string.entry_count, harvests.size),
                icon = Icons.Default.Agriculture,
                tab = FarmTab.Harvest,
            )
        }
        item { SectionTitle(stringResource(R.string.harvest_management_title)) }
        items(harvests) { harvest ->
            val crop = state.crops.firstOrNull { it.id == harvest.cropId }?.name.orEmpty()
            DetailCard(
                title = stringResource(R.string.harvest_title_format, crop),
                subtitle = stringResource(R.string.harvest_subtitle_format, harvest.harvestDate, harvest.managementNotes),
                value = formatKg(harvest.quantityKg),
                onClick = { onEditHarvest(harvest) },
            )
        }
    }
}

@Composable
fun SalesScreen(
    state: FarmUiState,
    selectedMonth: Int?,
    selectedYear: String,
    onMonthSelected: (Int?) -> Unit,
    onYearSelected: (String) -> Unit,
    theme: ModuleThemeColors,
    onImportSection: (DataSection) -> Unit,
    onExportSection: (DataSection) -> Unit,
    onEditSale: (SaleEntity) -> Unit,
) {
    val sales = state.sales.filterByPeriod(selectedMonth, selectedYear) { it.saleDate }
    val screenPadding = dimensionResource(R.dimen.space_20)
    LazyColumn(contentPadding = PaddingValues(start = screenPadding, end = screenPadding, bottom = screenPadding), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12))) {
        item {
            SectionToolsCard(stringResource(R.string.search_sales), selectedMonth, selectedYear, state.availableYears, theme, onMonthSelected, onYearSelected)
        }
        item {
            SectionTotalCard(
                title = stringResource(R.string.sales_total),
                primaryValue = rupees(sales.sumOf { it.totalIncome }),
                secondaryValue = stringResource(R.string.entry_count, sales.size),
                icon = Icons.Default.ShoppingCart,
                tab = FarmTab.Sales,
            )
        }
        items(sales) { sale ->
            val crop = state.crops.firstOrNull { it.id == sale.cropId }?.name.orEmpty()
            DetailCard(
                title = stringResource(R.string.sale_title_format, sale.buyerName, crop),
                subtitle = stringResource(R.string.sale_subtitle_format, sale.saleDate, formatKg(sale.quantityKg), rupees(sale.pricePerKg), sale.buyerPhone),
                value = rupees(sale.totalIncome),
                onClick = { onEditSale(sale) },
            )
        }
    }
}

@Composable
fun ReportsScreen(state: FarmUiState, onShare: () -> Unit) {
    val screenPadding = dimensionResource(R.dimen.space_20)
    LazyColumn(contentPadding = PaddingValues(screenPadding), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12))) {
        item {
            HeroCard(
                title = stringResource(R.string.offline_report_title),
                value = rupees(state.profitLoss),
                subtitle = stringResource(R.string.report_subtitle_format, rupees(state.totalIncome), rupees(state.totalExpense)),
                colors = listOf(Color(0xFF00695C), Color(0xFF26A69A), Color(0xFFFFA000)),
            )
        }
        item {
            Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Share, null)
                Spacer(Modifier.size(dimensionResource(R.dimen.space_8)))
                Text(stringResource(R.string.action_share_latest_excel))
            }
        }
    }
}
