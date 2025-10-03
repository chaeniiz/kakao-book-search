package com.chaeniiz.kakaobooksearch.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.kakaobooksearch.presentation.main.Screen

@Composable
fun BottomTabBar(
    currentRoute: String?,
    onTabClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.component_bottom_tab_search)
                )
            },
            label = { Text(stringResource(R.string.component_bottom_tab_search)) },
            selected = currentRoute == Screen.BookList.route,
            onClick = { onTabClick(Screen.BookList.route) }
        )
        
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.component_bottom_tab_favorite)
                )
            },
            label = { Text(stringResource(R.string.component_bottom_tab_favorite)) },
            selected = currentRoute == Screen.FavoriteList.route,
            onClick = { onTabClick(Screen.FavoriteList.route) }
        )
    }
}
