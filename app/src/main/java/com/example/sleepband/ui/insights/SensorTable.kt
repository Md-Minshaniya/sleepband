package com.example.sleepband.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sleepband.signal.SensorData

@Composable
fun SensorTable(rows: List<SensorData>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        item { RowHeader() }

        items(rows) { row ->
            RowItem(row)
        }
    }
}

@Composable
fun RowHeader() {
    Row(Modifier.fillMaxWidth()) {
        Header("TIME")
        Header("BVP")
        Header("AX")
        Header("AY")
        Header("AZ")
        Header("HR")
        Header("IBI")
    }
}

@Composable
fun RowItem(d: SensorData) {
    Row(Modifier.fillMaxWidth()) {
        Cell(d.timestamp.toString())
        Cell(d.bvp.toString())
        Cell(d.accX.toString())
        Cell(d.accY.toString())
        Cell(d.accZ.toString())
        Cell(d.hr.toString())
        Cell(d.ibi.toString())
    }
}

@Composable
fun RowScope.Cell(text: String) {
    Text(text, modifier = Modifier.weight(1f).padding(4.dp))
}

@Composable
fun RowScope.Header(text: String) {
    Text(text, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
}
