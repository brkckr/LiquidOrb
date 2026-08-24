package com.brkckr.liquidorb.ui.component

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import com.brkckr.liquidorb.model.OrbParams
import com.brkckr.liquidorb.ui.util.PARAMETRIC_SHADER_CODE

/**
 * renders a parametric liquid orb using agsl shader.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun LiquidOrb(
    params: OrbParams,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
) {
    val liquidShader = remember { RuntimeShader(PARAMETRIC_SHADER_CODE) }
    val shaderBrush = remember(liquidShader) { ShaderBrush(liquidShader) }
    val time by rememberInfiniteTransition(label = "time").animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000000, easing = LinearEasing)
        ),
        label = "time_animation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        liquidShader.setFloatUniform("iResolution", size.width, size.height)
        liquidShader.setFloatUniform("iTime", time)

        // motion & shape parameters
        liquidShader.setFloatUniform("styleId", params.styleId)
        liquidShader.setFloatUniform("glassEnabled", if (params.glassEnabled) 1f else 0f)
        liquidShader.setFloatUniform("speed", params.speed)
        liquidShader.setFloatUniform("radius", params.radius)
        liquidShader.setFloatUniform("contourDeform", params.contourDeform)
        liquidShader.setFloatUniform("warp", params.warp)
        liquidShader.setFloatUniform("zoom", params.zoom)
        liquidShader.setFloatUniform("sharp", params.sharp)
        liquidShader.setFloatUniform("edgeSoftness", params.edgeSoftness)
        liquidShader.setFloatUniform("ridgeAmt", params.ridgeAmt)

        // lighting & glass parameters
        liquidShader.setFloatUniform("shade", params.shade)
        liquidShader.setFloatUniform("sheen", params.sheen)
        liquidShader.setFloatUniform("gloss", params.gloss)
        liquidShader.setFloatUniform("glassOpacity", params.glassOpacity)
        liquidShader.setFloatUniform("exposure", params.exposure)
        liquidShader.setFloatUniform("edgeGlow", params.edgeGlow)

        // metal & texture parameters
        liquidShader.setFloatUniform("bandDensity", params.bandDensity)
        liquidShader.setFloatUniform("chromaticShift", params.chromaticShift)
        liquidShader.setFloatUniform("metalScale", params.metalScale)
        liquidShader.setFloatUniform("metalStretch", params.metalStretch)
        liquidShader.setFloatUniform("metalAngle", params.metalAngle)
        liquidShader.setFloatUniform("metalOffset", params.metalOffset)
        liquidShader.setFloatUniform("metalPhase", params.metalPhase)
        liquidShader.setFloatUniform("metalEvolution", params.metalEvolution)
        liquidShader.setFloatUniform("metalRoughness", params.metalRoughness)
        liquidShader.setFloatUniform("metalDepth", params.metalDepth)

        // shell parameters
        liquidShader.setFloatUniform("shellMidAlpha", params.shellMidAlpha)
        liquidShader.setFloatUniform("shellEdgeAlpha", params.shellEdgeAlpha)

        // colors
        liquidShader.setFloatUniform("colorA", params.colorA.red, params.colorA.green, params.colorA.blue)
        liquidShader.setFloatUniform("colorB", params.colorB.red, params.colorB.green, params.colorB.blue)
        liquidShader.setFloatUniform("colorC", params.colorC.red, params.colorC.green, params.colorC.blue)
        liquidShader.setFloatUniform("colorD", params.colorD.red, params.colorD.green, params.colorD.blue)
        liquidShader.setFloatUniform("highlightColor", params.highlightColor.red, params.highlightColor.green, params.highlightColor.blue)
        liquidShader.setFloatUniform("shellInner", params.shellInner.red, params.shellInner.green, params.shellInner.blue)
        liquidShader.setFloatUniform("shellMid", params.shellMid.red, params.shellMid.green, params.shellMid.blue)
        liquidShader.setFloatUniform("shellEdge", params.shellEdge.red, params.shellEdge.green, params.shellEdge.blue)
        liquidShader.setFloatUniform("sheenColor", params.sheenColor.red, params.sheenColor.green, params.sheenColor.blue)
        liquidShader.setFloatUniform("specColor", params.specColor.red, params.specColor.green, params.specColor.blue)

        val finalCanvasColor = backgroundColor ?: params.canvasColor
        liquidShader.setFloatUniform("canvasColor", finalCanvasColor.red, finalCanvasColor.green, finalCanvasColor.blue)

        liquidShader.setFloatUniform("glowColor", params.glowColor.red, params.glowColor.green, params.glowColor.blue)

        drawRect(brush = shaderBrush)
    }
}
