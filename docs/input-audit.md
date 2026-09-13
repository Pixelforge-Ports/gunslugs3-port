# Input audit

Inventoried all 562 supplied files by SHA256, size and signature. Every member of gunslugs3.dat (2,747 entries) and webcache.zip (9 entries) passed CRC inspection. Detailed private reports are under build/audit and reproducible with tools/inspect_input.py.

Inspected startup, graphics/framebuffer handling, timing, keyboard controls, profile and save methods and optional social integration. Binary assets and unrelated vendor classes were fingerprinted, not individually reverse engineered. Decompiled game classes were used only for private local inspection and are excluded from distribution. No supplied EXE or Windows installer was run.

This older build has no Linux ARM64 natives. The host loads pinned libGDX 1.12.1 and LWJGL 3.3.3 libraries before the original archive. The game prints an inlined libGDX 1.9.10 version string; class-load logs verify the replacement runtime actually in use. The original game archive is not rewritten.
The other Windows installation files and bundled JRE are not used by the port.
