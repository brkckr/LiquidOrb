package com.brkckr.liquidorb.model

import androidx.compose.ui.graphics.Color

/**
 * parameters for the parametric liquid orb shader.
 */
data class OrbParams(
    val style: String,
    val styleId: Float, // used to identify the animation style in shader
    val glassEnabled: Boolean,
    val speed: Float,
    val radius: Float,
    val contourDeform: Float,
    val bandDensity: Float,
    val chromaticShift: Float,
    val metalScale: Float,
    val metalStretch: Float,
    val metalAngle: Float,
    val metalOffset: Float,
    val metalPhase: Float,
    val metalEvolution: Float,
    val metalRoughness: Float,
    val metalDepth: Float,
    val zoom: Float,
    val warp: Float,
    val ridgeAmt: Float,
    val sharp: Float,
    val shade: Float,
    val sheen: Float,
    val gloss: Float,
    val glassOpacity: Float,
    val shellMidAlpha: Float,
    val shellEdgeAlpha: Float,
    val exposure: Float,
    val edgeSoftness: Float,
    val edgeGlow: Float,
    val colorA: Color,
    val colorB: Color,
    val colorC: Color,
    val colorD: Color,
    val highlightColor: Color,
    val shellInner: Color,
    val shellMid: Color,
    val shellEdge: Color,
    val sheenColor: Color,
    val specColor: Color,
    val canvasColor: Color,
    val glowColor: Color,
)

/**
 * available preview modes in the editor.
 */
enum class PreviewMode { ORB, SCENE }
