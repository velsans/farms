package com.farmmanager.export

import com.farmmanager.data.CropEntity
import com.farmmanager.data.ExpenseEntity
import com.farmmanager.data.FarmSnapshot
import com.farmmanager.data.HarvestEntity
import com.farmmanager.data.SaleEntity
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

class ExcelManager {
    fun export(snapshot: FarmSnapshot, outputStream: OutputStream) {
        XSSFWorkbook().use { workbook ->
            workbook.writeCrops(snapshot.crops)
            workbook.writeExpenses(snapshot.expenses)
            workbook.writeHarvests(snapshot.harvests)
            workbook.writeSales(snapshot.sales)
            workbook.write(outputStream)
        }
    }

    fun import(inputStream: InputStream): FarmSnapshot {
        XSSFWorkbook(inputStream).use { workbook ->
            return FarmSnapshot(
                crops = workbook.readCrops(),
                expenses = workbook.readExpenses(),
                harvests = workbook.readHarvests(),
                sales = workbook.readSales(),
            )
        }
    }

    private fun Workbook.writeCrops(crops: List<CropEntity>) {
        val sheet = createSheet("Crops")
        sheet.createRow(0).write("id", "name", "variety", "fieldName", "area", "season", "sowingDate", "notes")
        crops.forEachIndexed { index, crop ->
            sheet.createRow(index + 1).write(
                crop.id,
                crop.name,
                crop.variety,
                crop.fieldName,
                crop.area,
                crop.season,
                crop.sowingDate,
                crop.notes,
            )
        }
        sheet.autoSizeAll(8)
    }

    private fun Workbook.writeExpenses(expenses: List<ExpenseEntity>) {
        val sheet = createSheet("Expenses")
        sheet.createRow(0).write("id", "cropId", "category", "applicationRound", "amount", "expenseDate", "notes")
        expenses.forEachIndexed { index, expense ->
            sheet.createRow(index + 1).write(
                expense.id,
                expense.cropId,
                expense.category,
                expense.applicationRound ?: "",
                expense.amount,
                expense.expenseDate,
                expense.notes,
            )
        }
        sheet.autoSizeAll(7)
    }

    private fun Workbook.writeHarvests(harvests: List<HarvestEntity>) {
        val sheet = createSheet("Harvests")
        sheet.createRow(0).write("id", "cropId", "harvestDate", "quantityKg", "managementNotes")
        harvests.forEachIndexed { index, harvest ->
            sheet.createRow(index + 1).write(
                harvest.id,
                harvest.cropId,
                harvest.harvestDate,
                harvest.quantityKg,
                harvest.managementNotes,
            )
        }
        sheet.autoSizeAll(5)
    }

    private fun Workbook.writeSales(sales: List<SaleEntity>) {
        val sheet = createSheet("Sales")
        sheet.createRow(0).write("id", "cropId", "saleDate", "quantityKg", "pricePerKg", "totalIncome", "buyerName", "buyerPhone", "notes")
        sales.forEachIndexed { index, sale ->
            sheet.createRow(index + 1).write(
                sale.id,
                sale.cropId,
                sale.saleDate,
                sale.quantityKg,
                sale.pricePerKg,
                sale.totalIncome,
                sale.buyerName,
                sale.buyerPhone,
                sale.notes,
            )
        }
        sheet.autoSizeAll(9)
    }

    private fun Workbook.readCrops(): List<CropEntity> =
        getSheet("Crops")?.dropHeader()?.map { row ->
            CropEntity(
                id = row.longAt(0),
                name = row.stringAt(1),
                variety = row.stringAt(2),
                fieldName = row.stringAt(3),
                area = row.doubleAt(4),
                season = row.stringAt(5),
                sowingDate = row.stringAt(6),
                notes = row.stringAt(7),
            )
        }.orEmpty()

    private fun Workbook.readExpenses(): List<ExpenseEntity> =
        getSheet("Expenses")?.dropHeader()?.map { row ->
            ExpenseEntity(
                id = row.longAt(0),
                cropId = row.longAt(1),
                category = row.stringAt(2),
                applicationRound = row.optionalIntAt(3),
                amount = row.doubleAt(4),
                expenseDate = row.stringAt(5),
                notes = row.stringAt(6),
            )
        }.orEmpty()

    private fun Workbook.readHarvests(): List<HarvestEntity> =
        getSheet("Harvests")?.dropHeader()?.map { row ->
            HarvestEntity(
                id = row.longAt(0),
                cropId = row.longAt(1),
                harvestDate = row.stringAt(2),
                quantityKg = row.doubleAt(3),
                managementNotes = row.stringAt(4),
            )
        }.orEmpty()

    private fun Workbook.readSales(): List<SaleEntity> =
        getSheet("Sales")?.dropHeader()?.map { row ->
            SaleEntity(
                id = row.longAt(0),
                cropId = row.longAt(1),
                saleDate = row.stringAt(2),
                quantityKg = row.doubleAt(3),
                pricePerKg = row.doubleAt(4),
                buyerName = row.stringAt(6),
                buyerPhone = row.stringAt(7),
                notes = row.stringAt(8),
            )
        }.orEmpty()

    private fun Row.write(vararg values: Any) {
        values.forEachIndexed { index, value ->
            val cell = createCell(index)
            when (value) {
                is Number -> cell.setCellValue(value.toDouble())
                else -> cell.setCellValue(value.toString())
            }
        }
    }

    private fun org.apache.poi.ss.usermodel.Sheet.autoSizeAll(columns: Int) {
        repeat(columns) { autoSizeColumn(it) }
    }

    private fun org.apache.poi.ss.usermodel.Sheet.dropHeader(): List<Row> =
        (1..lastRowNum).mapNotNull { rowIndex -> getRow(rowIndex) }

    private fun Row.stringAt(index: Int): String = getCell(index)?.displayValue().orEmpty()

    private fun Row.doubleAt(index: Int): Double = getCell(index)?.numericValue() ?: 0.0

    private fun Row.longAt(index: Int): Long = doubleAt(index).toLong()

    private fun Row.optionalIntAt(index: Int): Int? =
        getCell(index)?.displayValue()?.takeIf { it.isNotBlank() }?.toDoubleOrNull()?.toInt()

    private fun Cell.numericValue(): Double =
        when (cellType) {
            CellType.NUMERIC -> numericCellValue
            CellType.STRING -> stringCellValue.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

    private fun Cell.displayValue(): String =
        when (cellType) {
            CellType.NUMERIC -> {
                val number = numericCellValue
                if (number % 1.0 == 0.0) number.toLong().toString() else number.toString()
            }
            CellType.STRING -> stringCellValue
            CellType.BOOLEAN -> booleanCellValue.toString()
            else -> ""
        }
}
