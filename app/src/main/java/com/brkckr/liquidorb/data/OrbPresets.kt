package com.brkckr.liquidorb.data

import com.brkckr.liquidorb.model.OrbParams
import com.brkckr.liquidorb.ui.util.colorFromHex

/**
 * predefined presets for the liquid orb.
 */
object OrbPresets {
    // common base values for all presets
    private val basePreset = OrbParams(
        style = "base", styleId = 15f, glassEnabled = true, speed = 1f, radius = 0.72f,
        contourDeform = 0f, bandDensity = 2f, chromaticShift = 0.42f, metalScale = 0.77f,
        metalStretch = 0.23f, metalAngle = 65f, metalOffset = 0f, metalPhase = 0f,
        metalEvolution = 1f, metalRoughness = 0.22f, metalDepth = 0.25f, zoom = 0.3f,
        warp = 3f, ridgeAmt = 0.5f, sharp = 2.2f, shade = 0.3f, sheen = 0.36f,
        gloss = 0.28f, glassOpacity = 0.42f, shellMidAlpha = 0.2f, shellEdgeAlpha = 0.22f,
        exposure = 1f, edgeSoftness = 0.005f, edgeGlow = 0f,
        colorA = colorFromHex("#F7FBFF"), colorB = colorFromHex("#D6E8F7"),
        colorC = colorFromHex("#A8C8F0"), colorD = colorFromHex("#6F9EE8"),
        highlightColor = colorFromHex("#FFFFFF"), shellInner = colorFromHex("#FFFFFF"),
        shellMid = colorFromHex("#D6E8F7"), shellEdge = colorFromHex("#6F9EE8"),
        sheenColor = colorFromHex("#EAF4FF"), specColor = colorFromHex("#DCEAFF"),
        canvasColor = colorFromHex("#000000"), glowColor = colorFromHex("#6F9EE8")
    )

    // available orb styles
    val styles: Map<String, OrbParams> = mapOf(
        "Siri Wave" to basePreset.copy(
            style = "Siri Wave", styleId = 9f, speed = 0.82f, zoom = 0.36f, warp = 3.2f,
            sharp = 2.2f, shade = 0.12f, sheen = 0.28f, gloss = 0.24f,
            glassOpacity = 0.44f, shellMidAlpha = 0.18f, shellEdgeAlpha = 0.18f, exposure = 2f,
            colorA = colorFromHex("#FFD86B"), colorB = colorFromHex("#82F4FF"),
            colorC = colorFromHex("#FF7BD5"), colorD = colorFromHex("#8E6CFF"),
            shellMid = colorFromHex("#9BF4FF"), shellEdge = colorFromHex("#C5A9FF"),
            canvasColor = colorFromHex("#030409"), glowColor = colorFromHex("#956CFF")
        ),

        "Voice Membrane" to basePreset.copy(
            style = "Voice Membrane", styleId = 19f, speed = 0.95f, radius = 0.7f,
            contourDeform = 0.1f, zoom = 0.36f, warp = 2.6f, ridgeAmt = 0.46f,
            shade = 0.08f, sheen = 0.22f, gloss = 0.36f, glassOpacity = 0.48f,
            shellMidAlpha = 0.18f, shellEdgeAlpha = 0.2f, exposure = 1.35f,
            colorA = colorFromHex("#09030E"), colorB = colorFromHex("#CE2CCB"),
            colorC = colorFromHex("#FF5C71"), colorD = colorFromHex("#7B53FF"),
            highlightColor = colorFromHex("#FFD9F0"), shellMid = colorFromHex("#E48BFF"),
            shellEdge = colorFromHex("#FF7890"), sheenColor = colorFromHex("#FFF1FA"),
            specColor = colorFromHex("#E7D9FF"), canvasColor = colorFromHex("#020105"),
            glowColor = colorFromHex("#CE2CCB")
        ),

        "Aurora Veil" to basePreset.copy(
            style = "Aurora Veil", styleId = 10f, speed = 3f, contourDeform = 0.08f,
            zoom = 0.4f, warp = 4.2f, ridgeAmt = 0.62f, sharp = 2.1f, shade = 0.18f, exposure = 1.18f,
            colorA = colorFromHex("#030816"), colorB = colorFromHex("#20F0B6"),
            colorC = colorFromHex("#32A8FF"), colorD = colorFromHex("#A34BFF"),
            shellMid = colorFromHex("#32A8FF"), shellEdge = colorFromHex("#20F0B6"),
            canvasColor = colorFromHex("#010207"), glowColor = colorFromHex("#20F0B6")
        ),

        "Plasma" to basePreset.copy(
            style = "Plasma", styleId = 11f, speed = 1.32f, contourDeform = 0.05f,
            zoom = 0.55f, warp = 5.4f, ridgeAmt = 0.78f, sharp = 4.2f, shade = 0.16f, exposure = 1.25f,
            colorA = colorFromHex("#06020E"), colorB = colorFromHex("#0099FF"),
            colorC = colorFromHex("#258BFF"), colorD = colorFromHex("#1375FF"),
            shellInner = colorFromHex("#FFFFFF"), shellMid = colorFromHex("#1951C2"),
            shellEdge = colorFromHex("#00E9FF"), sheenColor = colorFromHex("#EAF4FF"),
            specColor = colorFromHex("#DCEAFF"), canvasColor = colorFromHex("#020105"),
            glowColor = colorFromHex("#0099FF")
        ),

        "Liquid Chrome" to basePreset.copy(
            style = "Liquid Chrome", styleId = 12f, speed = 2f, zoom = 0.36f, warp = 3.8f,
            ridgeAmt = 0.44f, sharp = 5.2f, shade = 0.58f, exposure = 1.08f,
            colorA = colorFromHex("#FFFFFF"), colorB = colorFromHex("#B9C0CA"),
            colorC = colorFromHex("#343A43"), colorD = colorFromHex("#030405"),
            shellMid = colorFromHex("#B9C0CA"), shellEdge = colorFromHex("#FFFFFF"),
            canvasColor = colorFromHex("#050608"), glowColor = colorFromHex("#FFFFFF")
        ),

        "Iridescent Opal" to basePreset.copy(
            style = "Iridescent Opal", styleId = 13f, speed = 1.5f, zoom = 0.3f, warp = 2.8f,
            ridgeAmt = 0.36f, sharp = 2f, shade = 0.1f, sheen = 0.3f, gloss = 0.26f,
            glassOpacity = 0.38f, shellMidAlpha = 0.2f, shellEdgeAlpha = 0.2f, exposure = 1.12f,
            colorA = colorFromHex("#FFF6E8"), colorB = colorFromHex("#6EF2CF"),
            colorC = colorFromHex("#FF91D8"), colorD = colorFromHex("#756BFF"),
            shellMid = colorFromHex("#CDE5FF"), shellEdge = colorFromHex("#D9C8FF"),
            canvasColor = colorFromHex("#07080D"), glowColor = colorFromHex("#9E8CFF")
        ),

        "Spectrum" to basePreset.copy(
            style = "Spectrum", styleId = 14f, speed = 1.8f, contourDeform = 0.03f,
            zoom = 0.46f, warp = 4.4f, ridgeAmt = 0.72f, shade = 0.06f, sheen = 0.26f,
            gloss = 0.24f, glassOpacity = 0.4f, shellMidAlpha = 0.18f, shellEdgeAlpha = 0.18f, exposure = 1.5f,
            colorA = colorFromHex("#FFFFFF"), colorB = colorFromHex("#1677FF"),
            colorC = colorFromHex("#F249A0"), colorD = colorFromHex("#35E6B2"),
            shellMid = colorFromHex("#66E8FF"), shellEdge = colorFromHex("#D26CFF"),
            canvasColor = colorFromHex("#03040A"), glowColor = colorFromHex("#1677FF")
        ),

        "Frost Flow" to basePreset.copy(
            style = "Frost Flow", styleId = 15f, speed = 2.22f, contourDeform = 0.04f,
            zoom = 0.36f, warp = 3.7f, ridgeAmt = 0.45f, sharp = 2.05f, shade = 0.3f,
            sheen = 0.34f, gloss = 0.28f, glassOpacity = 0.42f, shellMidAlpha = 0.2f,
            shellEdgeAlpha = 0.22f, exposure = 1f,
            colorA = colorFromHex("#F7FBFF"), colorB = colorFromHex("#D6E8F7"),
            colorC = colorFromHex("#A8C8F0"), colorD = colorFromHex("#6F9EE8"),
            shellMid = colorFromHex("#D6E8F7"), shellEdge = colorFromHex("#6F9EE8"),
            canvasColor = colorFromHex("#000000"), glowColor = colorFromHex("#6F9EE8")
        ),

        "Crystal Drop" to basePreset.copy(
            style = "Crystal Drop", styleId = 20f, speed = 0.9f, radius = 0.74f,
            contourDeform = 0.08f, zoom = 0.48f, warp = 2.65f, ridgeAmt = 0.42f,
            sharp = 2.4f, shade = 0.16f, sheen = 0.22f, gloss = 0.42f, glassOpacity = 0.66f,
            shellMidAlpha = 0.32f, shellEdgeAlpha = 0.24f, exposure = 1.24f,
            colorA = colorFromHex("#020B1D"), colorB = colorFromHex("#0756B8"),
            colorC = colorFromHex("#1EC8FF"), colorD = colorFromHex("#DDFBFF"),
            highlightColor = colorFromHex("#EAFBFF"), shellInner = colorFromHex("#F6FDFF"),
            shellMid = colorFromHex("#4FD7FF"), shellEdge = colorFromHex("#466DFF"),
            sheenColor = colorFromHex("#DDFBFF"), specColor = colorFromHex("#A8D9FF"),
            canvasColor = colorFromHex("#010207"), glowColor = colorFromHex("#168DFF")
        ),

        "Violet Ember" to basePreset.copy(
            style = "Violet Ember", styleId = 21f, speed = 1.12f, radius = 0.72f,
            contourDeform = 0.04f, zoom = 0.58f, warp = 4.7f, ridgeAmt = 0.73f,
            sharp = 3.3f, shade = 0.18f, sheen = 0.2f, gloss = 0.34f, glassOpacity = 0.62f,
            shellMidAlpha = 0.28f, shellEdgeAlpha = 0.24f, exposure = 1.28f,
            colorA = colorFromHex("#100016"), colorB = colorFromHex("#4A0E8F"),
            colorC = colorFromHex("#A52EFF"), colorD = colorFromHex("#F1A7FF"),
            highlightColor = colorFromHex("#FFD6FF"), shellInner = colorFromHex("#FCF5FF"),
            shellMid = colorFromHex("#C257FF"), shellEdge = colorFromHex("#6C2DFF"),
            sheenColor = colorFromHex("#F8E6FF"), specColor = colorFromHex("#D4B7FF"),
            canvasColor = colorFromHex("#030006"), glowColor = colorFromHex("#A52EFF")
        ),

        "Chromatic Metal" to basePreset.copy(
            style = "Chromatic Metal", styleId = 22f, speed = 1.12f, radius = 0.72f,
            bandDensity = 2f, chromaticShift = 0.42f, metalScale = 0.77f,
            metalStretch = 0.23f, metalAngle = 65f, metalOffset = 0f, metalPhase = 0f,
            metalEvolution = 1f, metalRoughness = 0.16f, metalDepth = 0.38f,
            shade = 0.1f, sheen = 0.14f, gloss = 0.46f, glassOpacity = 0.54f,
            shellMidAlpha = 0.2f, shellEdgeAlpha = 0.16f, exposure = 1.08f,
            colorA = colorFromHex("#FBFCFB"), colorB = colorFromHex("#7F8683"),
            colorC = colorFromHex("#D6DAD8"), colorD = colorFromHex("#33373A"),
            highlightColor = colorFromHex("#FFFFFF"), shellInner = colorFromHex("#F7FCFF"),
            shellMid = colorFromHex("#6EDCFF"), shellEdge = colorFromHex("#FF806D"),
            sheenColor = colorFromHex("#F7FCFF"), specColor = colorFromHex("#D9F3FF"),
            canvasColor = colorFromHex("#050606"), glowColor = colorFromHex("#BDEFFF")
        )
    )
}
