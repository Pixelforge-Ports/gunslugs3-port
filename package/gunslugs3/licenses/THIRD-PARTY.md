# Replacement runtime components

The original commercial game is not covered by the host's MIT license.

libGDX 1.12.1 and gdx-jnigen-loader 2.3.1: Apache 2.0. LWJGL 3.3.3: BSD. GLFW, jemalloc, stb and FreeType retain the notices supplied in this folder. libGDX native archives include multiple operating systems; the runtime selects ARM64 Linux on the handheld.

JLayer 1.0.1-gdx and JOrbis 0.0.17: LGPL; their source archives and LGPL text are included. OpenAL Soft 1.23.1: LGPL; the upstream notice and source release archive are included. The native OpenAL version string was checked against 1.23.1. Libraries remain separate replaceable JARs under runtime/lib; no game code is included in these runtime JARs.

Pinned artifact URLs and SHA256 values are recorded in tools/runtime-lock.json and tools/license-lock.json in the source package. They identify Maven Central and the original upstream distributions. The game data is separately supplied by its owner.
