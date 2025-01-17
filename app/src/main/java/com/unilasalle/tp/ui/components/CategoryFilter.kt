package com.unilasalle.tp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unilasalle.tp.services.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun CategoryFilter(onSelected: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(listOf<String>()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    val apiService = ApiService.createService()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            categories = apiService.getCategories()
        }
    }

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedCategory,
                fontSize = 18.sp,
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown Icon"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    selectedCategory = "All"
                    expanded = false
                    onSelected("All")
                },
                text = { Text("All") }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        selectedCategory = category
                        expanded = false
                        onSelected(category)
                    },
                    text = { Text(category) }
                )
            }
        }
    }
}