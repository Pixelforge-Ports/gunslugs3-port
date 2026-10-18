## Notes

Thanks to [Orangepixel](https://orangepixel.net/) for creating **Gunslugs 3 Rogue Tactics**. Choose stealth or gunfire as you complete missions through procedurally generated enemy territory. PortMaster adaptation by **Pixelforge Ports (Ronax)**.

## Get gunslugs3.dat from GOG

1. Open [Gunslugs 3 Rogue Tactics on GOG](https://www.gog.com/en/game/gunslugs_3_rogue_tactics) in your owned library and download the **full Windows offline backup installer** for the supported build (1.0.10b). Download every accompanying `.bin` part, if listed, and keep them beside the `.exe`. Use the full installer, not a patch or the Galaxy installer.
2. Run the installer on Windows and open the installed game directory. Find `gunslugs3.dat` beside the game executable. Enable file extensions in Explorer so its name is visible.
3. Copy that file unchanged into the installed port at `<ports directory>/gunslugs3/gamedata/gunslugs3.dat`. Keep its exact name, capitalization and spaces. The Windows EXE and bundled Windows Java runtime are not needed.

Alternatively, extract your full offline installer using [innoextract](https://constexpr.org/innoextract/).
Run `innoextract -d extracted "your-full-offline-installer.exe"`, then locate `gunslugs3.dat`
inside the extracted files (usually `extracted/app/`) and copy it to the same destination.
Installer layouts vary; use Windows installation if your extractor cannot read that installer.
Do not unpack or rename the game archive itself.

No game-data conversion is required on a PC or handheld. The handheld verifies the supplied
archive and creates its save folders. It cannot generate the purchased game data from nothing.

Supported archive SHA-256 (`gunslugs3.dat`):

```text
8e9f0784c8824119ac5572baa09e9d93e317ffd0a6ad3f1de1be4a76e61bebdf
```

Compare it with `Get-FileHash -Algorithm SHA256 "gunslugs3.dat"` in PowerShell,
or `sha256sum "gunslugs3.dat"` on Linux. A different build needs a compatibility check.

## Installation

1. Update PortMaster. Put **Gunslugs3.zip** in PortMaster's `autoinstall/` directory, then open PortMaster to install it. Connect to the network to download Java 17 and Westonpack if they are not installed yet.
2. Copy the owned file to **`<ports directory>/gunslugs3/gamedata/gunslugs3.dat`**.
3. Launch **Gunslugs 3 Rogue Tactics** from your firmware's ports menu.

The launcher detects display size and supports **640x480**, **720x480**, **720x720**, **1024x768**, **1280x720**, and other valid PortMaster dimensions. Displays narrower than 16:9 use a **3:2 game view** to show more of the surrounding level than a 4:3 or square view. **1280x720** retains the default **16:9** view; wider displays retain the expanded view. Scaling preserves proportions without stretching or cropping.

| Display | Game aspect ratio | Visible game area |
|---|---|---|
| 720x480 | 3:2 | 720x480, fills the screen |
| 640x480 | 3:2 | 640x427, centered with small top/bottom bars |
| 720x720 | 3:2 | 720x480, centered with 120-pixel top/bottom bars |
| 1024x768 | 3:2 | 1024x683, centered with small top/bottom bars |
| 1280x720 | 16:9 | 1280x720, fills the screen |

The game receives a logical 1080x720 view for 3:2 or 1280x720 for 16:9, keeping its vertical scale consistent. Mouse and menu coordinates follow the same scaled viewport. If display detection is wrong, put the actual size, such as `720x480`, in `gunslugs3/resolution.txt`; use `auto` or remove the file to restore automatic detection.

Back up **`gunslugs3/saves/`** before updating. If startup fails, check **`gunslugs3/log.txt`**. When reporting a problem, include the device, firmware version, resolution, reproduction steps, and log. Keep purchased game files private.

## Controls

| Control | Keyboard input / action |
|---|---|
| D-pad up | UP / Up / navigate |
| D-pad down | DOWN / Down / navigate |
| D-pad left | LEFT / Left / navigate |
| D-pad right | RIGHT / Right / navigate |
| A | X / Action / confirm |
| B | UP / Up / navigate |
| X | ENTER |
| Y | Z |
| L1 | A |
| R1 | X / Action / confirm |
| L2 | F1 |
| R2 | A |
| Start | O / Options |
| Select | F1 |
| Left stick | Same directions as the D-pad |
| Select + Start | Exit; save through the game first |

Use the game's default keyboard bindings. Start sends **O** for options.

## Build the PortMaster package

Requires Python 3.9+ and JDK 17 or newer. **No purchased JAR or DAT is required to compile
the host or build the ZIP.** From this source directory, on Windows:

```bat
python tools/build.py --jdk "C:\Program Files\Java\jdk-17"
```

Replace the quoted path with your installed JDK directory, for example `jdk-26.0.2.1`.
Use double quotes in Windows Command Prompt. On Linux:

```sh
python3 tools/build.py --jdk "/path/to/installed/jdk-17"
```

The first build downloads checksum-pinned public compile dependencies. Later builds may add
`--offline` to use the cache. Handwritten `compile-api/` declarations are compile-only;
only `org/portmaster/gunslugs3/` host classes go into `gunslugs3-host.jar`.
Gunslugs 3 also downloads checksum-pinned public ARM64 runtime libraries and their license/source files; `--offline` requires those already cached in the package tree.

The only release artifact is **`dist/Gunslugs3.zip`**, a universal BYO-data ZIP.
The build also prepares **`ports/gunslugs3/`** in the PortMaster source submission layout.
It never packages the owned game archive, MewnBase data, Windows runtimes or personal saves.
After editing package documentation or controls, rebuild with:

```sh
python tools/build.py --package-only
python tools/verify_package.py
```

`--package-only` requires a previously built host. The optional `--game-jar` argument checks
a supplied archive's fingerprint; it does not participate in compilation. Downloading a
public compile dependency does not supply the commercial game. Copy the owned files after installing.

Run `bash tests/verify_display.sh` for display-helper checks. Run `python tests/verify_launcher.py` for lifecycle checks. These tests use
mock runtimes and do not mount or run games. See `VALIDATION.md` for the recorded checks
and `testing_thread.txt` for the Discord testing post. Upload source files using Git;
`build/`, `dist/`, generated `ports/` and owned data are excluded by `.gitignore`.

The Discord draft stays in source `testing_thread.txt`; it is not installed by the ZIP.
