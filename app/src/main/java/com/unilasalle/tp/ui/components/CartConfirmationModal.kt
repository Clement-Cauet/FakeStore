package com.unilasalle.tp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unilasalle.tp.services.database.entities.User
import kotlinx.coroutines.launch

/**
 * Cart confirmation modal.
 *
 * @param totalAmount The total amount.
 * @param user The user.
 * @param onDismiss The on dismiss.
 * @param onConfirm The on confirm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartConfirmationModal(totalAmount: Double, user: User?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            sheetState.show()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Confirm Purchase",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            user?.let {
                Text(text = "Email: ${it.email}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total: \$${String.format("%.2f", totalAmount)}",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm")
            }
        }
    }
}