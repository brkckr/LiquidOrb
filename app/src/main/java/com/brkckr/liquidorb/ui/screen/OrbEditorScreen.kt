package com.brkckr.liquidorb.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brkckr.liquidorb.data.OrbPresets
import com.brkckr.liquidorb.model.OrbParams
import com.brkckr.liquidorb.model.PreviewMode
import com.brkckr.liquidorb.ui.component.LiquidOrb
import com.brkckr.liquidorb.ui.theme.BackgroundDark
import com.brkckr.liquidorb.ui.theme.BorderDefault
import com.brkckr.liquidorb.ui.theme.BorderLighter
import com.brkckr.liquidorb.ui.theme.BorderSelected
import com.brkckr.liquidorb.ui.theme.SurfaceDark
import com.brkckr.liquidorb.ui.theme.SurfaceLighter
import com.brkckr.liquidorb.ui.theme.SurfaceSelected
import com.brkckr.liquidorb.ui.theme.TextMuted
import com.brkckr.liquidorb.ui.theme.TextSecondary

/**
 * main screen for browsing and previewing liquid orb presets.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OrbEditorScreen() {
    val presetList = remember { OrbPresets.styles.values.toList() }
    var activeParams by remember { mutableStateOf(presetList.first()) }
    var previewMode by remember { mutableStateOf(PreviewMode.ORB) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // upper half: 3x4 grid area (shifted down for camera/notch)
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxWidth()
                    .background(BackgroundDark),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(presetList) { param ->
                            PresetItem(
                                param = param,
                                isSelected = param.style == activeParams.style,
                            ) { activeParams = param }
                        }
                    }
                }
            }

            // lower half: large preview and mode toggle
            Column(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                PreviewToggle(
                    currentMode = previewMode,
                    onModeChanged = { previewMode = it }
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(
                        targetState = activeParams,
                        animationSpec = tween(600),
                        label = "active_params_crossfade"
                    ) { currentParams ->
                        AnimatedContent(
                            targetState = previewMode,
                            transitionSpec = {
                                (fadeIn(tween(400)) + scaleIn(initialScale = 0.8f))
                                    .togetherWith(fadeOut(tween(400)) + scaleOut(targetScale = 1.2f))
                            },
                            label = "preview_mode_animation",
                            modifier = Modifier.fillMaxSize()
                        ) { targetMode ->
                            when (targetMode) {
                                PreviewMode.ORB -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LiquidOrb(
                                            params = currentParams,
                                            modifier = Modifier.fillMaxSize(),
                                            backgroundColor = Color.Black
                                        )
                                    }
                                }
                                PreviewMode.SCENE -> AiAgentScene(param = currentParams)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * compact card design for the preset grid.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun PresetItem(
    param: OrbParams,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) BorderSelected else BorderDefault

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            LiquidOrb(
                params = param,
                backgroundColor = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = param.style,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * toggle button to switch between orb and scene preview modes.
 */
@Composable
private fun PreviewToggle(
    currentMode: PreviewMode,
    onModeChanged: (PreviewMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .padding(4.dp)
    ) {
        PreviewToggleItem(
            text = "Orb",
            isSelected = currentMode == PreviewMode.ORB,
            onClick = { onModeChanged(PreviewMode.ORB) }
        )

        PreviewToggleItem(
            text = "Scene",
            isSelected = currentMode == PreviewMode.SCENE,
            onClick = { onModeChanged(PreviewMode.SCENE) }
        )
    }
}

@Composable
private fun PreviewToggleItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) SurfaceSelected else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 13.sp
        )
    }
}

/**
 * renders the orb within an ai agent scene context.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AiAgentScene(param: OrbParams) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "shimmer_offset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            TextMuted,
            Color.White.copy(alpha = 0.8f),
            TextMuted
        ),
        start = Offset(shimmerOffset - 300f, 0f),
        end = Offset(shimmerOffset, 100f),
        tileMode = TileMode.Clamp
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(SurfaceLighter)
                .border(1.dp, BorderLighter, RoundedCornerShape(percent = 50))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LiquidOrb(
                params = param,
                modifier = Modifier.size(120.dp),
                backgroundColor = SurfaceLighter
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Thinking...",
                style = TextStyle(
                    brush = shimmerBrush,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
