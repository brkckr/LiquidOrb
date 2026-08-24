https://github.com/user-attachments/assets/27778197-c27a-4800-a03d-2aa6360e5704


# Liquid Orb for Android

WebGPU liquid orb shaders ported to Android using Jetpack Compose and AGSL.

## 💡 About
This project ports the WebGPU liquid orb concept originally shared by [@LerSent001](https://github.com/LerSent001/orb) to Android, implementing interactive visual presets and real-time parameter tuning natively on mobile hardware.

## 🛠️ Technical Details
- **AGSL Rendering**: Utilizes `RuntimeShader` and Android Graphics Shading Language for high-performance pixel processing.
- **Optics & Shading**: Features 3D raymarching mathematics, Fractal Brownian Motion (FBM) noise fields, and analytic frequency-domain Gaussian diffusion.
- **Glass Refraction**: Implements signed-distance refraction profiles with chromatic aberration and dual directional edge lighting.
- **UI Architecture**: Built with Jetpack Compose, Material 3 components, and zero-render overlay optimizations via `AnimatedVisibility`.

## 🚀 Requirements
- Android 13 (API 33+) or higher for AGSL `RuntimeShader` support.



