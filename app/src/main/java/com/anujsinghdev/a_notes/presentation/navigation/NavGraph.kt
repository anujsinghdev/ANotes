package com.anujsinghdev.a_notes.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anujsinghdev.a_notes.presentation.archive_notes.ArchiveNotesScreen
import com.anujsinghdev.a_notes.presentation.deleted_notes.DeletedNotesScreen
import com.anujsinghdev.a_notes.presentation.folder_notes.FolderNotesScreen
import com.anujsinghdev.a_notes.presentation.note_detail.NoteDetailScreen
import com.anujsinghdev.a_notes.presentation.notes_list.NotesListScreen

sealed class Screen(val route: String) {
    data object NotesList : Screen("notes_list")
    data object Archive : Screen("archive")
    data object Deleted : Screen("deleted")
    data object Folder : Screen("folder/{folderId}/{folderName}") {
        fun createRoute(folderId: Long, folderName: String) = "folder/$folderId/$folderName"
    }
    data object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long? = null) = if (noteId != null) {
            "note_detail/$noteId"
        } else {
            "note_detail/new"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    // iOS-like emphasized easing curve for ultra-smooth motion
    // This curve mimics Apple's spring animation timing
    val emphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // Shorter duration (300ms) matches iOS navigation timing
    val animDuration = 300

    // Spring spec for super natural motion (like iOS)
    val springSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route,
        // Forward navigation: Slide in from right + fade + subtle scale
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + scaleIn(
                initialScale = 0.95f, // Subtle zoom effect like iOS
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            )
        },
        // Forward navigation exit: Slide out to left + fade + scale down
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + scaleOut(
                targetScale = 0.95f, // Slight scale down for depth
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            )
        },
        // Back navigation: Slide in from left (previous screen comes back)
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            )
        },
        // Back navigation exit: Slide out to right (current screen exits)
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            ) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(
                    durationMillis = animDuration,
                    easing = emphasizedEasing
                )
            )
        }
    ) {
        composable(Screen.NotesList.route) {
            NotesListScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onCreateNote = {
                    navController.navigate(Screen.NoteDetail.createRoute())
                },
                onNavigateToArchive = {
                    navController.navigate(Screen.Archive.route)
                },
                onNavigateToDeleted = {
                    navController.navigate(Screen.Deleted.route)
                },
                onNavigateToFolder = { folder ->
                    navController.navigate(Screen.Folder.createRoute(folder.id, folder.name))
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }

        composable(Screen.Archive.route) {
            ArchiveNotesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                }
            )
        }

        composable(Screen.Deleted.route) {
            DeletedNotesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Folder.route,
            arguments = listOf(
                navArgument("folderId") { type = NavType.LongType },
                navArgument("folderName") { type = NavType.StringType }
            )
        ) {
            FolderNotesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                }
            )
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            NoteDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
