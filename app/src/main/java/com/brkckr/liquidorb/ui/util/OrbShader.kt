package com.brkckr.liquidorb.ui.util

/**
 * agsl shader code for the parametric liquid orb.
 */
const val PARAMETRIC_SHADER_CODE = """
    uniform float2 iResolution;
    uniform float iTime;
    
    uniform float styleId;
    uniform float glassEnabled;
    uniform float speed, radius, contourDeform, warp, zoom, sharp, edgeSoftness;
    uniform float shade, sheen, gloss, glassOpacity, exposure, edgeGlow;
    uniform float bandDensity, chromaticShift, metalScale, metalStretch;
    uniform float metalAngle, metalOffset, metalPhase, metalEvolution, metalRoughness, metalDepth;
    uniform float shellMidAlpha, shellEdgeAlpha, ridgeAmt;
    
    uniform half3 colorA, colorB, colorC, colorD, highlightColor;
    uniform half3 shellInner, shellMid, shellEdge, sheenColor, specColor;
    uniform half3 canvasColor, glowColor;

    const float GL_FU = 0.88172043;
    const float GL_BSIG_CLEAR = 0.018;
    const float GL_KA = 6.0;
    const float GL_KG = 4.1209;
    const float GL_KR = 0.32;
    const float GL_GH = 1.73205081;

    float2 glsRotate(float2 p, float angle) {
        float c = cos(angle); float s = sin(angle);
        return float2(c * p.x - s * p.y, s * p.x + c * p.y);
    }

    float lqHash(float2 pIn) {
        float2 p = fract(pIn * float2(123.34, 456.21));
        p += float2(dot(p, p + float2(45.32, 45.32)));
        return fract(p.x * p.y);
    }

    float lqNoise(float2 p) {
        float2 i = floor(p); float2 f = fract(p);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(lqHash(i), lqHash(i + float2(1.0, 0.0)), f.x),
                   mix(lqHash(i + float2(0.0, 1.0)), lqHash(i + float2(1.0, 1.0)), f.x), f.y);
    }

    float2 lqFbm(float2 pIn, float bs) {
        float2 p = pIn; float s = 0.0; float a = 0.5; float m = 0.0; float vr = 0.0;
        float e = -GL_KA * bs * bs; float g = 1.0;
        for (int i = 0; i < 5; i++) {
            float b = exp(e * g);
            s += a * (0.5 + b * (lqNoise(p) - 0.5));
            vr += a * a * (1.0 - b * b);
            m += a; a *= 0.5; g *= GL_KG;
            p = float2(0.8 * p.x - 0.6 * p.y, 0.6 * p.x + 0.8 * p.y) * 2.03;
        }
        return float2(s / m, GL_KR * sqrt(vr) / m);
    }

    float lqRidge(float v, float k) { return pow(clamp(1.0 - abs(v * 2.0 - 1.0), 0.0, 1.0), k); }
    float lqRidgeS(float2 vs, float k) { float d = GL_GH * vs.y; return (lqRidge(vs.x - d, k) + 4.0 * lqRidge(vs.x, k) + lqRidge(vs.x + d, k)) / 6.0; }
    float lqStepS(float2 vs, float a, float b) { float d = GL_GH * vs.y; return (smoothstep(a, b, vs.x - d) + 4.0 * smoothstep(a, b, vs.x) + smoothstep(a, b, vs.x + d)) / 6.0; }

    half3 lqRamp(float v, half3 cA, half3 cB, half3 cC, half3 cD) {
        half3 c = mix(cA, cB, half(smoothstep(0.0, 0.45, v)));
        c = mix(c, cC, half(smoothstep(0.38, 0.72, v)));
        return mix(c, cD, half(smoothstep(0.68, 1.0, v)));
    }

    half3 glsFinishPresetFluid(half3 colorIn, float2 p) {
        half3 c = colorIn;
        c = mix(c, highlightColor, half(shade * 0.22 * smoothstep(0.15, 1.15, dot(p, float2(-0.32, 0.78)))));
        c *= half(1.0 - shade * 0.34 * smoothstep(-0.1, 1.2, dot(p, float2(0.45, -0.62))));
        c *= half(1.0 - shade * 0.22 * smoothstep(0.72, 1.08, length(p)));
        return clamp(c, half3(0.0), half3(1.0));
    }

    // --- contour and wave animation ---
    float2 glsContourWave(float angle, float t) {
        if (styleId == 19.0) {
            float wave = sin(angle * 2.0 + t * 0.27) * 0.72 + sin(angle * 4.0 - t * 0.16 + 2.1) * 0.28;
            float slope = cos(angle * 2.0 + t * 0.27) * 1.44 + cos(angle * 4.0 - t * 0.16 + 2.1) * 1.12;
            return float2(wave, slope);
        }
        float wave = sin(angle * 3.0 + t * 0.62) * 0.52 + sin(angle * 5.0 - t * 0.41 + 1.7) * 0.31 + sin(angle * 2.0 + t * 0.23 + 3.1) * 0.17;
        float slope = cos(angle * 3.0 + t * 0.62) * 1.56 + cos(angle * 5.0 - t * 0.41 + 1.7) * 1.55 + cos(angle * 2.0 + t * 0.23 + 3.1) * 0.34;
        return float2(wave, slope);
    }
    
    float glsContourStrength() { return (styleId >= 18.5) ? 0.11 : ((styleId >= 15.5) ? 0.16 : 0.09); }
    
    float glsContourScale(float2 uv, float t, float amount) {
        if (amount <= 0.0) return 1.0;
        float2 contour = glsContourWave(atan(uv.y, uv.x), t);
        return 1.0 + clamp(amount, 0.0, 1.0) * glsContourStrength() * contour.x;
    }
    
    float2 glsContourNormal(float2 uv, float rad, float t, float amount) {
        float distance = length(uv);
        if (distance <= 0.0001) return float2(0.0);
        float2 radial = uv / distance;
        float2 contour = glsContourWave(atan(uv.y, uv.x), t);
        float slope = clamp(amount, 0.0, 1.0) * glsContourStrength() * contour.y;
        float2 tangent = float2(-radial.y, radial.x);
        return normalize(radial - tangent * (rad * slope / distance));
    }

    // refraction profile with clamp
    float glsRefractionProfile(float t) {
        float depth = clamp(t, 0.0, 1.0);
        float circular = sqrt(max(1.0 - (1.0 - depth) * (1.0 - depth), 0.0));
        return 1.0 - circular;
    }

    // ==========================================
    // orb models (spatial functions)
    // ==========================================

    // 1. siri wave (9)
    float2 glsSiriBand(float2 q, float drift, float phaseOffset, float amplitude, float mainY, float envelope, float softness) {
        float y = amplitude * envelope * sin(q.x * 1.0 + drift + phaseOffset);
        float distanceToLine = abs(q.y - y);
        float line = 0.018 / (sqrt(distanceToLine * distanceToLine + softness * softness) + 0.026);
        float bandDistance = max(0.0, max(q.y - max(mainY, y), min(mainY, y) - q.y));
        float band = 0.018 / (bandDistance + 0.075);
        return float2(line, band);
    }

    half3 glsSiriFluid(float2 p, float t) {
        float scale = 0.74 + zoom * 0.34; float2 q = p / scale;
        float envelopeBase = cos(1.570796 * min(abs(0.9 * q.x), 1.0));
        float envelope = envelopeBase * envelopeBase;
        float low = 0.5 + 0.5 * cos(t * 0.37); float mid = 0.5 + 0.5 * sin(t * 0.51 + 1.2); float high = 0.5 + 0.5 * cos(t * 0.73 + 2.1);
        float drift = t * 2.4;
        float mainAmp = 0.25 + ridgeAmt * 0.075 + low * 0.018; float bandAmp = mainAmp + mid * 0.025 + high * 0.018;
        float mainY = mainAmp * envelope * sin(q.x * 1.1 + drift);
        float separation = 1.85 + warp * 0.2 + mid * 0.28; float softness = 0.035 + (1.0 - ridgeAmt) * 0.018 + mid * 0.006;

        float2 b0 = glsSiriBand(q, drift, -separation, bandAmp, mainY, envelope, softness);
        float2 b1 = glsSiriBand(q, drift, -separation * 0.34, bandAmp, mainY, envelope, softness);
        float2 b2 = glsSiriBand(q, drift, separation * 0.34, bandAmp, mainY, envelope, softness);
        float2 b3 = glsSiriBand(q, drift, separation, bandAmp, mainY, envelope, softness);
        
        float w0 = b0.x + b0.y; float w1 = b1.x + b1.y; float w2 = b2.x + b2.y; float w3 = b3.x + b3.y;
        float total = w0 + w1 + w2 + w3;
        float d0 = w0 * w0; float d1 = w1 * w1; float d2 = w2 * w2; float d3 = w3 * w3;
        float domTotal = d0 + d1 + d2 + d3;
        
        half3 spectral = (colorA * half(d0) + colorC * half(d1) + colorB * half(d2) + colorD * half(d3)) / half(max(domTotal, 0.0001));
        float energy = (1.0 - exp(-total * 0.58)) * envelope;
        float whiteCore = exp(-abs(q.y - mainY) * abs(q.y - mainY) / 0.0028) * envelope;
        
        half3 atmosphere = mix(colorD, colorB, half(smoothstep(-0.7, 0.7, q.y))) * half(0.018);
        half3 color = atmosphere + spectral * half(energy * 1.14);
        color += highlightColor * half(whiteCore * (0.18 + 0.1 * low));
        return glsFinishPresetFluid(color / (half3(1.0) + color * half(0.18)), p);
    }

    // 2. voice membrane (19)
    half3 glsVoiceWaveFluid(float2 p, float t) {
        float scale = 0.76 + zoom * 0.34; float2 q = p / scale;
        float rimEnvelope = pow(max(1.0 - q.x * q.x, 0.0), 0.72);
        float drift = t * 0.82; float amplitude = 0.2 + warp * 0.018;
        float mainY = rimEnvelope * (amplitude * sin(q.x * 1.48 + drift) + 0.055 * sin(q.x * 3.2 - drift * 0.43 + 1.1));
        float distance = q.y - mainY;
        float width = 0.11 + (1.0 - ridgeAmt) * 0.075;
        
        float membrane = exp(-distance * distance / max(width * width, 0.001)) * rimEnvelope;
        float upperVeil = exp(-(distance - 0.105) * (distance - 0.105) / max(width * width * 2.4, 0.001)) * rimEnvelope;
        float lowerVeil = exp(-(distance + 0.115) * (distance + 0.115) / max(width * width * 2.8, 0.001)) * rimEnvelope;
        float crest = exp(-distance * distance / 0.0026) * rimEnvelope;
        float depth = sqrt(max(1.0 - clamp(dot(p, p), 0.0, 1.0), 0.0));
        
        half3 color = mix(colorA * half(0.7), colorD * half(0.34), half(smoothstep(-0.82, 0.82, q.y)));
        color = mix(color, colorB, half(upperVeil * 0.7));
        color = mix(color, colorC, half(lowerVeil * 0.62));
        color += mix(colorB, colorC, half(0.46)) * half(membrane * 0.34);
        color += highlightColor * half(crest * 0.14);
        return glsFinishPresetFluid(color * half(0.58 + 0.42 * depth), p);
    }

    // 3. aurora veil (10)
    float glsAuroraLayer(float2 p, float t, float offset) {
        float drift = t * 0.18 + offset * 2.5;
        float wave1 = sin(p.x * (2.0 + warp * 0.13) + drift + offset * 6.0) * 0.25;
        float wave2 = sin(p.x * 3.7 + drift * 1.3 + offset * 4.0) * 0.12;
        float wave3 = sin(p.x * 7.2 + drift * 0.7 + offset * 8.0) * 0.055;
        float noiseValue = lqFbm(float2(p.x * 1.6 + drift * 0.35, p.y * 0.8 + offset * 3.0), 0.018).x;
        float center = offset * 0.46 + wave1 + wave2 + wave3 + (noiseValue - 0.5) * 0.28;
        float dist = abs(p.y - center);
        float glow = exp(-dist * dist * (13.0 - 5.0 * ridgeAmt)); 
        float shimmer = lqFbm(float2(p.x * 4.0 + t * 0.22, p.y * 7.0 + offset * 5.0), 0.012).x;
        return glow * (0.64 + 0.36 * shimmer);
    }
    
    half3 glsAuroraFluid(float2 p, float t) {
        float2 q = p * (0.82 + zoom * 0.58);
        float l0 = glsAuroraLayer(q, t, -0.72); float l1 = glsAuroraLayer(q, t, 0.0); float l2 = glsAuroraLayer(q, t, 0.72);
        half3 color = colorA * half(0.46 + 0.18 * (q.y + 1.0));
        color += colorB * half(l0 * 1.3); color += colorC * half(l1 * 1.15); color += colorD * half(l2 * 1.2);
        color += mix(colorB, colorD, half(0.5)) * half(min(l0 * l2, l1) * 0.65);
        float2 starUv = (q + float2(1.0)) * 18.0; float starHash = lqHash(floor(starUv));
        float2 fractStar = fract(starUv) - float2(0.5);
        float stars = step(0.965, starHash) * exp(-dot(fractStar, fractStar) * 90.0) * (0.55 + 0.45 * sin(t * (1.0 + starHash * 2.0) + starHash * 6.28));
        color += highlightColor * half(stars * (1.0 - clamp(l0 + l1 + l2, 0.0, 1.0)));
        return glsFinishPresetFluid(color / (half3(1.0) + color * half(0.28)), p);
    }

    // 4. plasma (11)
    float glsNeuroShape(float2 pIn, float t) {
        float2 p = pIn * (0.34 + 0.08 * zoom); float2 sineAccum = float2(0.0); float2 result = float2(0.0); float sc = 8.0;
        const float c1 = 0.5403023; const float s1 = 0.84147098;
        for (int j = 0; j < 11; j++) {
            p = float2(c1 * p.x - s1 * p.y, s1 * p.x + c1 * p.y);
            sineAccum = float2(c1 * sineAccum.x - s1 * sineAccum.y, s1 * sineAccum.x + c1 * sineAccum.y);
            float2 layer = p * sc + float(j) + sineAccum - t * 0.34;
            sineAccum += sin(layer); result += (0.5 + 0.5 * cos(layer)) / sc; sc *= 1.16;
        }
        return result.x + result.y;
    }
    
    half3 glsPlasmaFluid(float2 p, float t) {
        float shape = glsNeuroShape(p, t);
        float phase = shape * (10.0 + warp) + p.x * 1.7 - p.y * 1.3 - t * 0.52;
        float ridgeWidth = 0.62 - 0.24 * ridgeAmt;
        float primary = pow(abs(cos(phase)), max(1.3, sharp * ridgeWidth));
        float secondary = pow(abs(cos(phase * 0.53 + atan(p.y, p.x) * 2.0 + t * 0.21)), max(1.6, sharp * (ridgeWidth + 0.1)));
        float filaments = max(primary, secondary * 0.64);
        float core = pow(primary, 4.0);
        float polarity = 0.5 + 0.5 * sin(phase * 0.37 + shape * 3.0);
        
        half3 color = mix(colorA * half(0.42), colorD * half(0.48), half(polarity * 0.46));
        color = mix(color, colorB, half(filaments * 0.72));
        color = mix(color, colorC, half(core * 0.68));
        color += highlightColor * half(pow(core, 3.0) * 0.16);
        return glsFinishPresetFluid(color / (half3(1.0) + color * half(0.34)), p);
    }

    // 5. liquid chrome (12)
    half3 glsChromeFluid(float2 p, float t) {
        float2 q = p * (1.0 + zoom * 0.35); float amplitude = 0.028 * warp;
        for (int i = 1; i <= 9; i++) {
            float fi = float(i);
            q.x += amplitude / fi * cos(fi * 2.7 * q.y + t * 0.46);
            q.y += amplitude / fi * cos(fi * 3.1 * q.x - t * 0.4);
        }
        float denominator = max(abs(sin(t * 0.24 - q.y - q.x)), 0.045);
        float flare = clamp(1.0 / denominator, 0.0, 18.0);
        float metal = smoothstep(1.15, 7.5, flare);
        float fold = 0.5 + 0.5 * cos((q.x - q.y) * (3.2 + sharp * 0.28) + t * 0.32);
        float value = clamp(metal * 0.74 + fold * 0.36, 0.0, 1.0);
        half3 color = lqRamp(value, colorD, colorC, colorB, colorA);
        color = mix(color, colorA, half(pow(metal, 5.0) * 0.62));
        return glsFinishPresetFluid(color, p);
    }

    // 6. iridescent opal (13)
    half3 glsOpalFluid(float2 p, float t) {
        float2 q = p * (0.8 + zoom * 0.64); float complexity = 0.76 + warp * 0.085;
        float d = -t * 0.42; float a = 0.0;
        for (int i = 0; i < 8; i++) {
            float fi = float(i);
            a += cos(fi - d - a * q.x * complexity); d += sin(q.y * fi * complexity + a);
        }
        d += t * 0.42;
        float2 c1 = cos(q * float2(d, a)) * 0.6 + float2(0.4); float c2 = cos(a + d) * 0.5 + 0.5;
        float3 interference = 0.5 + 0.5 * cos(float3(c1.x, c1.y, c2) * cos(float3(d, a, 2.5)) * 0.5 + float3(0.5));
        float tone = fract(interference.r * 0.37 + interference.g * 0.51 + interference.b * 0.73 + c1.x * 0.22 - c1.y * 0.15);
        half3 color = lqRamp(tone, colorB, colorC, colorD, colorA);
        color = mix(color, colorA, half(0.16 + 0.1 * interference.b));
        return glsFinishPresetFluid(color / (half3(1.0) + color * half(0.16)), p);
    }

    // 7. spectrum (14)
    float glsSpectrumHeight(float2 q, float t, float frequency, float phaseOffset, float amplitude) {
        float x = q.x * 2.15; float envelope = pow(4.0 / (4.0 + x * x), 4.0);
        float breathing = 0.82 + 0.18 * sin(t * 0.48 + phaseOffset * 0.7);
        float wave = abs(sin(frequency * x - t * 1.36 + phaseOffset));
        return envelope * amplitude * breathing * (0.28 + 0.72 * wave);
    }
    
    float glsSpectrumLayer(float2 q, float height, float softness) {
        return (1.0 - smoothstep(max(height - softness, 0.0), height + softness, abs(q.y))) * smoothstep(0.0, 0.045, height);
    }
    
    half3 glsSpectrumFluid(float2 p, float t) {
        float scale = 0.74 + zoom * 0.34; float2 q = p / scale;
        float amplitude = 0.26 + ridgeAmt * 0.27; float frequency = 0.72 + warp * 0.095; float softness = 0.026 + (1.0 - ridgeAmt) * 0.032;
        float h0 = glsSpectrumHeight(q, t, frequency * 0.82, -1.2, amplitude * 0.72);
        float h1 = glsSpectrumHeight(q, t, frequency, 0.45, amplitude);
        float h2 = glsSpectrumHeight(q, t, frequency * 1.17, 2.05, amplitude * 0.82);
        float l0 = glsSpectrumLayer(q, h0, softness); float l1 = glsSpectrumLayer(q, h1, softness); float l2 = glsSpectrumLayer(q, h2, softness);
        float envelope = pow(4.0 / (4.0 + (q.x * 2.15) * (q.x * 2.15)), 4.0);
        float support = exp(-q.y * q.y / 0.00072) * envelope;
        float total = l0 + l1 + l2;
        half3 spectral = (colorB * half(l0) + colorC * half(l1) + colorD * half(l2)) / half(max(total, 0.001));
        half3 color = colorD * half(0.025) + spectral * half(1.0 - exp(-total * 0.86));
        color += colorA * half(support * 0.58);
        return glsFinishPresetFluid(color / (half3(1.0) + color * half(0.2)), p);
    }

    // 8. frost flow (15)
    half3 glsFrostFluid(float2 p, float t) {
        float2 q = p * (0.66 + zoom * 0.92); q.y += t * 0.055;
        float blur = 0.011 + 0.006 * zoom;
        float2 warpField = float2(lqFbm(q * 1.14 + float2(t * 0.055, 0.0), blur).x, lqFbm(q * 1.14 + float2(6.8, -t * 0.048), blur).x);
        float2 warped = q + (warpField - float2(0.5)) * (0.28 + warp * 0.17);
        float2 body = lqFbm(warped * 1.48 + float2(t * 0.032, -t * 0.02), blur * 1.48);
        float veins = lqRidgeS(lqFbm(warped * 2.36 + float2(3.1, -t * 0.024), blur * 2.36), sharp);
        float value = mix(lqStepS(body, 0.1, 0.9), clamp(veins * 0.8 + body.x * 0.46, 0.0, 1.0), ridgeAmt);
        half3 c = lqRamp(value, colorA, colorB, colorC, colorD);
        return glsFinishPresetFluid(mix(c, colorA, half(0.08 * smoothstep(0.62, 0.92, body.x))), p);
    }

    // 9. blue drop (20)
    half3 glsBlueDropFluid(float2 p, float t) {
        float depth = sqrt(max(1.0 - clamp(dot(p, p), 0.0, 1.0), 0.0));
        float2 q = p * mix(0.72, 1.0, depth * 0.62 + 0.38);
        q = glsRotate(q, -0.24 + 0.06 * sin(t * 0.17));
        float scale = 1.0 + zoom * 1.12; float blur = 0.012 + 0.006 * zoom;
        float driftA = lqFbm(q * 1.28 + float2(t * 0.095, -t * 0.034), blur * 1.28).x;
        float driftB = lqFbm(glsRotate(q, 1.08) * 1.62 + float2(-t * 0.042, t * 0.078), blur * 1.62).x;
        float2 flowed = q + float2(driftA - 0.5, driftB - 0.5) * (0.24 + warp * 0.1);
        flowed.x += sin(flowed.y * 2.15 + t * 0.24) * (0.035 + warp * 0.012);
        flowed.y += sin(flowed.x * 1.38 - t * 0.18) * (0.045 + warp * 0.01);
        float body = lqFbm(flowed * scale + float2(t * 0.025, -t * 0.018), blur * scale).x;
        float marble = lqRidgeS(lqFbm(flowed * (1.72 + zoom * 0.9) + float2(2.7, -t * 0.035), blur * (1.72 + zoom * 0.9)), 0.8 + sharp * 0.46);
        float value = clamp(mix(body, body * 0.62 + marble * 0.58, ridgeAmt), 0.0, 1.0);
        half3 color = lqRamp(value, colorA, colorB, colorC, colorD);
        float light = pow(max(dot(normalize(float3(p, depth)), normalize(float3(-0.48, 0.62, 0.92))), 0.0), 3.2);
        color = mix(color, highlightColor, half(light * (0.035 + 0.05 * shade)));
        return glsFinishPresetFluid(color * half(0.74 + 0.26 * depth), p);
    }

    // 10. violet ember (21)
    half3 glsVioletEmberFluid(float2 p, float t) {
        float scale = 1.08 + zoom * 1.18; float blur = 0.011 + 0.005 * zoom; float radius = length(p);
        float twist = t * 0.055 + radius * (0.72 + warp * 0.11) + 0.08 * sin(t * 0.31 + radius * 4.0);
        float2 q = glsRotate(p * scale, twist);
        float low = lqFbm(q * 1.18 + float2(t * 0.068, -t * 0.105), blur * 1.18).x;
        float cross = lqFbm(glsRotate(q, -1.12) * 1.52 + float2(-t * 0.094, t * 0.042) + float2(low * 1.35, -low * 0.72), blur * 1.52).x;
        float2 warped = q + float2(low - 0.5, cross - 0.5) * (0.3 + warp * 0.12);
        float melt = lqFbm(warped * 1.34 + float2(cross * 1.48, low * 1.12), blur * 1.34).x;
        float veins = lqRidgeS(lqFbm(warped * (2.05 + zoom * 0.72) + float2(-2.1, t * 0.052), blur * (2.05 + zoom * 0.72)), 0.82 + sharp * 0.58);
        float heat = smoothstep(0.18, 0.92, melt * (0.72 - ridgeAmt * 0.16) + veins * (0.32 + ridgeAmt * 0.5));
        half3 color = lqRamp(heat, colorA, colorB, colorC, colorD);
        color *= half(0.94 + 0.06 * sin(t * 0.44 + melt * 5.0));
        color = mix(color, highlightColor, half(pow(veins, 4.0) * 0.045));
        return glsFinishPresetFluid(color, p);
    }

    // 11. chromatic metal (22)
    float glsChromaticMetalPhase(float2 p, float t) {
        float scale = max(metalScale, 0.05); float stretch = mix(0.48, 1.58, clamp(metalStretch, 0.0, 1.0));
        float2 q = glsRotate(p / scale, metalAngle * 0.017453); q = float2(q.x / stretch, q.y * stretch);
        float cycle = t * 0.46 + metalPhase * 6.283185; float ev = clamp(metalEvolution, 0.0, 2.0);
        q.x += sin(q.y * 1.86 - cycle) * 0.095 * ev; q.x += sin((q.x + q.y) * 1.28 + cycle * 2.0 + 1.4) * 0.045 * ev; q.y += sin(q.x * 1.52 + cycle + 0.8) * 0.07 * ev;
        float repeats = max(bandDensity, 1.0);
        return q.x * repeats * 2.18 + sin(q.y * (1.3 + repeats * 0.26) - cycle) * 0.56 * ev + sin((q.x - q.y) * 1.34 + cycle * 2.0 + 1.7) * 0.27 * ev + sin((q.x * 0.72 + q.y) * 2.1 - cycle * 3.0 + 0.35) * 0.11 * ev + sin(cycle) * 0.1 + sin(cycle * 3.0 + 0.7) * 0.035 + cycle + metalOffset * 6.283185;
    }
    
    half3 glsChromaticMetalSample(float2 p, float t) {
        float phase = glsChromaticMetalPhase(p, t); float r = clamp(metalRoughness, 0.0, 1.0); float d = clamp(metalDepth, 0.0, 1.0);
        float wave = 0.5 + 0.5 * cos(phase); float edge = 0.025 + r * 0.18;
        float tone = clamp(0.018 + mix(wave, smoothstep(0.5 - edge, 0.5 + edge, wave), 0.2 + d * 0.3) * (0.46 + d * 0.12) + pow(wave, mix(13.0, 4.0, r)) * (0.3 + d * 0.42) - pow(1.0 - wave, mix(9.0, 3.0, r)) * (0.07 + d * 0.11) + (sin(glsRotate(p / max(metalScale, 0.05), metalAngle * 0.017453).y * 146.0 + sin(glsRotate(p / max(metalScale, 0.05), metalAngle * 0.017453).x * 11.0) * 0.58) + 0.48 * sin(glsRotate(p / max(metalScale, 0.05), metalAngle * 0.017453).y * 317.0 - glsRotate(p / max(metalScale, 0.05), metalAngle * 0.017453).x * 5.0)) * (0.004 + r * 0.014), 0.0, 1.0);
        return lqRamp(tone, colorD, colorB, colorC, colorA);
    }
    
    half3 glsChromaticMetalFluid(float2 p, float t) {
        float2 split = glsRotate(float2(0.0, 1.0), metalAngle * 0.017453) * chromaticShift * 0.045;
        half3 redS = glsChromaticMetalSample(p + split, t); half3 neuS = glsChromaticMetalSample(p, t); half3 bluS = glsChromaticMetalSample(p - split, t);
        half3 optical = half3(redS.r, neuS.g, bluS.b);
        half3 color = mix(neuS, optical, half(clamp(chromaticShift * (0.72 + clamp(length(float3(optical - neuS)) * 4.0, 0.0, 1.0) * 0.28), 0.0, 1.0)));
        float glint = pow(clamp(0.018 + mix(0.5 + 0.5 * cos(glsChromaticMetalPhase(p, t)), smoothstep(0.5 - (0.025 + clamp(metalRoughness, 0.0, 1.0) * 0.18), 0.5 + (0.025 + clamp(metalRoughness, 0.0, 1.0) * 0.18), 0.5 + 0.5 * cos(glsChromaticMetalPhase(p, t))), 0.2 + clamp(metalDepth, 0.0, 1.0) * 0.3) * (0.46 + clamp(metalDepth, 0.0, 1.0) * 0.12) + pow(0.5 + 0.5 * cos(glsChromaticMetalPhase(p, t)), mix(13.0, 4.0, clamp(metalRoughness, 0.0, 1.0))) * (0.3 + clamp(metalDepth, 0.0, 1.0) * 0.42) - pow(1.0 - (0.5 + 0.5 * cos(glsChromaticMetalPhase(p, t))), mix(9.0, 3.0, clamp(metalRoughness, 0.0, 1.0))) * (0.07 + clamp(metalDepth, 0.0, 1.0) * 0.11), 0.0, 1.0), mix(12.0, 5.0, clamp(metalRoughness, 0.0, 1.0)));
        color = mix(color, highlightColor, half(glint * clamp(metalDepth, 0.0, 1.0) * 0.06));
        float3 normal = normalize(float3(p, sqrt(max(1.0 - clamp(dot(p, p), 0.0, 1.0), 0.0)))); float r = clamp(metalRoughness, 0.0, 1.0); float d = clamp(metalDepth, 0.0, 1.0);
        color *= half(0.86 + normal.z * 0.14);
        color = mix(color, highlightColor, half(pow(max(dot(normal, normalize(float3(-0.48, 0.62, 0.62))), 0.0), mix(7.0, 3.0, r)) * (0.05 + d * 0.13)));
        color = mix(color, colorC, half(pow(max(dot(normal, normalize(float3(0.7, -0.34, 0.63))), 0.0), mix(10.0, 4.0, r)) * (0.025 + d * 0.07)));
        color = mix(color, colorD, half(pow(1.0 - normal.z, 3.0) * (0.12 + d * 0.15)));
        color = mix(color, highlightColor, half(pow(1.0 - normal.z, 10.0) * (0.035 + d * 0.055)));
        return glsFinishPresetFluid(color, p);
    }

    half3 glsOver(half3 dst, half3 src, float a) { float k = clamp(a, 0.0, 1.0); return src * half(k) + dst * half(1.0 - k); }
    float glsHighlightLobe(float2 normal, float2 direction, float cut, float power) { return pow(clamp((dot(normal, direction) - cut) / max(1.0 - cut, 0.001), 0.0, 1.0), power); }

    // ==========================================
    // main render loop
    // ==========================================
    half4 main(float2 fragCoord) {
        float2 fc = float2(fragCoord.x, iResolution.y - fragCoord.y);
        float2 uv = (2.0 * fc - iResolution) / max(min(iResolution.x, iResolution.y), 1.0);
        float rad = max(radius, 0.05); float t = iTime * speed;
        
        float contourRad = rad * glsContourScale(uv, t, contourDeform);
        if (length(uv) > contourRad * (1.01 + max(edgeSoftness - 0.005, 0.0))) { return half4(canvasColor, 1.0); }

        float2 p = uv / contourRad; float pd = length(p);
        float clearFa = 1.0 - smoothstep(0.995, 1.04, pd);
        float2 normal = glsContourNormal(uv, rad, t, contourDeform);
        
        float edgeDepth = max(1.0 - pd, 0.0);
        float refractionWidth = 0.015 + 0.95 * clamp(shellMidAlpha, 0.0, 1.0);
        
        float refractionT = edgeDepth / max(refractionWidth, 0.001);
        float refractionProfile = pow(glsRefractionProfile(refractionT), 0.68);
        
        float2 refractedP = p - normal * (1.6 * clamp(glassOpacity, 0.0, 1.0) * refractionProfile);

        half3 fcol = half3(0.0);
        if (clearFa > 0.0) {
            float2 evalP = (glassEnabled > 0.5) ? refractedP : p;
            
            if (styleId == 9.0) fcol = glsSiriFluid(evalP, t);
            else if (styleId == 10.0) fcol = glsAuroraFluid(evalP, t);
            else if (styleId == 11.0) fcol = glsPlasmaFluid(evalP, t);
            else if (styleId == 12.0) fcol = glsChromeFluid(evalP, t);
            else if (styleId == 13.0) fcol = glsOpalFluid(evalP, t);
            else if (styleId == 14.0) fcol = glsSpectrumFluid(evalP, t);
            else if (styleId == 19.0) fcol = glsVoiceWaveFluid(evalP, t);
            else if (styleId == 20.0) fcol = glsBlueDropFluid(evalP, t);
            else if (styleId == 21.0) fcol = glsVioletEmberFluid(evalP, t);
            else if (styleId == 22.0) fcol = glsChromaticMetalFluid(evalP, t);
            else fcol = glsFrostFluid(evalP, t);
        }

        float lum = dot(float3(fcol), float3(0.213, 0.715, 0.072));
        half3 col = glsOver(canvasColor, clamp(half3(lum) + (fcol - half3(lum)) * half(1.22), half3(0.0), half3(1.0)), 0.99 * clearFa);

        if (glassEnabled > 0.5) {
            float surfaceBand = (1.0 - smoothstep(0.0, 0.026 + 0.055 * clamp(shellEdgeAlpha, 0.0, 1.0), edgeDepth)) * clearFa;
            float opticalRim = pow(surfaceBand, 1.8);
            col = glsOver(col, shellInner, opticalRim * glassOpacity * 0.45);

            float dispersion = opticalRim * clamp(gloss, 0.0, 2.0) * (0.8 + 0.8 * shellEdgeAlpha);
            col = glsOver(col, shellMid, dispersion * glsHighlightLobe(normal, normalize(float2(0.84, 0.54)), -0.32, 1.8));
            col = glsOver(col, shellEdge, dispersion * glsHighlightLobe(normal, normalize(float2(-0.62, -0.78)), -0.28, 2.0));

            col *= half(1.0 - opticalRim * (0.015 + 0.15 * shellEdgeAlpha) * (0.15 + 0.85 * max(dot(normal, float2(0.45, -0.89)), 0.0)));
            col = glsOver(col, sheenColor, opticalRim * glsHighlightLobe(normal, normalize(float2(-0.68, 0.73)), 0.2, 2.8) * clamp(sheen, 0.0, 2.0) * 1.4);
            col = glsOver(col, specColor, opticalRim * glsHighlightLobe(normal, normalize(float2(0.74, -0.67)), 0.4, 3.6) * clamp(sheen, 0.0, 2.0) * 1.0);
        }

        float ballA = 1.0 - smoothstep(0.99 - max(edgeSoftness - 0.005, 0.0), 1.01 + max(edgeSoftness - 0.005, 0.0), pd);
        return half4(clamp(col * half(max(exposure, 0.0)), half3(0.0), half3(1.0)) * half(ballA), 1.0);
    }
"""
