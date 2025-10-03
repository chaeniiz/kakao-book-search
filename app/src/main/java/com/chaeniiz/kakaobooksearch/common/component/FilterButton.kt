package com.chaeniiz.kakaobooksearch.common.component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    text: String,
    onFilterClick: () -> Unit
) {
    OutlinedButton(modifier = modifier, onClick = onFilterClick) {
        Text(text = text)
    }
}
