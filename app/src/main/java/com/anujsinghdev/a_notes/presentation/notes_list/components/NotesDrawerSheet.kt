package com.anujsinghdev.a_notes.presentation.notes_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anujsinghdev.a_notes.domain.model.Folder

@Composable
fun NotesDrawerSheet(
    folders: List<Folder>,
    onItemClick: (String) -> Unit,
    onFolderClick: (Folder) -> Unit,
    onCreateFolderClick: () -> Unit
) {
    ModalDrawerSheet {
        // Use LazyColumn to make the entire drawer content scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            item {
                Text(
                    text = "A Notes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 28.dp, top = 24.dp, bottom = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Primary (Notes)
                NavigationDrawerItem(
                    label = { Text("Notes") },
                    icon = { Icon(Icons.Default.Lightbulb, null) },
                    selected = true,
                    onClick = { onItemClick("Notes") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            // Folders Section (Dynamic)
            if (folders.isNotEmpty()) {
                item {
                    Text(
                        text = "Folders", // Renamed from "Labels" to "Folders"
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                // This efficiently renders the list of folders
                items(folders) { folder ->
                    NavigationDrawerItem(
                        label = { Text(folder.name) },
                        icon = { Icon(Icons.Default.Folder, null) },
                        selected = false,
                        onClick = { onFolderClick(folder) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }

            // Bottom Actions Section
            item {
                NavigationDrawerItem(
                    label = { Text("Create new folder") },
                    icon = { Icon(Icons.Default.CreateNewFolder, null) },
                    selected = false,
                    onClick = onCreateFolderClick,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Secondary Actions
                NavigationDrawerItem(
                    label = { Text("Archive") },
                    icon = { Icon(Icons.Default.Archive, null) },
                    selected = false,
                    onClick = { onItemClick("Archive") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Deleted") },
                    icon = { Icon(Icons.Default.Delete, null) },
                    selected = false,
                    onClick = { onItemClick("Deleted") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Backup Section
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Backup",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 28.dp, top = 8.dp, bottom = 8.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Import Notes") },
                    icon = { Icon(Icons.Default.Download, null) },
                    selected = false,
                    onClick = { onItemClick("Import") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Export Notes") },
                    icon = { Icon(Icons.Default.Upload, null) },
                    selected = false,
                    onClick = { onItemClick("Export") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Legal Section
                NavigationDrawerItem(
                    label = { Text("Terms & Conditions") },
                    icon = { Icon(Icons.Default.Description, null) },
                    selected = false,
                    onClick = { onItemClick("Terms") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Privacy Policy") },
                    icon = { Icon(Icons.Default.Policy, null) },
                    selected = false,
                    onClick = { onItemClick("Privacy") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contact Developer (Now scrolls into view at the bottom)
                NavigationDrawerItem(
                    label = { Text("Contact Developer") },
                    icon = { Icon(Icons.Default.Email, null) },
                    selected = false,
                    onClick = { onItemClick("Contact") },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}