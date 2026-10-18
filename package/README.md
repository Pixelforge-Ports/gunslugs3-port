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

The launcher detects display size and supports **640x480**, **720x480**, **720x720**, **1024x768**, **1280x720**, and other valid PortMaster dimensions while preserving aspect ratio. If detection is wrong, put the actual size, such as `720x480`, in `gunslugs3/resolution.txt`; use `auto` or remove the file to restore automatic detection.

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
