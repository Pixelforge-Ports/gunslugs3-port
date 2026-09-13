## Notes

Thanks to [Orangepixel](https://orangepixel.net/) for creating **Gunslugs 3 Rogue Tactics**. Choose stealth or gunfire as you complete missions through procedurally generated enemy territory.

Porter: **Pixelforge ports (Ronax)**.

This universal **Gunslugs3.zip** is a bring-your-own-data PortMaster package for compatible
**64-bit ARM Linux firmware**. Supply the owned game files below. PortMaster provides Java 17
and Westonpack. Device testing is requested for muOS on RG34XX SP and other RGXX models,
R36S with compatible firmware, and other ARM64 handhelds. A device name alone does not guarantee
a compatible 64-bit userspace or graphics driver.

## Get gunslugs3.dat from GOG

1. Open [Gunslugs 3 Rogue Tactics on GOG](https://www.gog.com/en/game/gunslugs_3_rogue_tactics) in your owned library and download the **full Windows offline backup installer** for the supported build (1.0.10b). Download every accompanying `.bin` part, if listed, and keep them beside the `.exe`. Use the full installer, not a patch or the Galaxy installer.
2. Run the installer on Windows and open the installed game directory. Find `gunslugs3.dat` beside the game executable. Enable file extensions in Explorer so its name is visible.
3. Copy that file unchanged into the installed port at `<ports directory>/gunslugs3/gunslugs3.dat`. Keep its exact name, capitalization and spaces. The Windows EXE and bundled Windows Java runtime are not needed.

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
2. Copy the owned file to **`<ports directory>/gunslugs3/gunslugs3.dat`**.
3. Launch **Gunslugs 3 Rogue Tactics** from your firmware's ports menu.

For manual installation on **muOS**, extract the ZIP on your computer and copy `Gunslugs 3 Rogue Tactics.sh`
to `<SD card>/roms/PORTS/`, and the `gunslugs3/` folder to `<SD card>/ports/` on the card
configured as the firmware's ports location. The required file is
**`<SD card>/ports/gunslugs3/gunslugs3.dat`**.

For **ArkOS/dArkOS and standard ports layouts**, extract the ZIP into your configured
ports directory (for example `/roms/ports/` or `/roms2/ports/`) so `Gunslugs 3 Rogue Tactics.sh` and
`gunslugs3/` are beside each other. Use the firmware's configured ports location; the launcher
uses PortMaster's `directory` value. Avoid creating an extra `Gunslugs3/` wrapper folder.

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

## Controller support

All input is supplied through PortMaster's gptokeyb2 and the shipped `.ini` mapping.
Update PortMaster before installing. Native Xbox 360 emulation is not enabled in this
host: its native controller path is disabled. The mapper's `-x` mode replaces keyboard
and mouse output and requires a working native controller backend in the game.
Do not add `-x` to this launcher; it would bypass the controls listed above.

## Display

The display helper accepts 640x480, 720x480, 720x720, 1024x768 and 1280x720, and other
valid dimensions supplied by PortMaster. The host preserves the game view's aspect ratio;
black borders may appear. This includes RG35XX/RG40XX/R36S, RG34XX/SP, CubeXX, TrimUI Brick
and Smart Pro display shapes when their firmware and hardware meet the runtime requirements.

If automatic detection is incorrect, create `gunslugs3/resolution.txt` containing the actual
size, for example `720x480`. Use `auto` or remove the file to restore automatic detection.

## Saves and troubleshooting

Back up **`gunslugs3/saves/`** before updates.
Read **`gunslugs3/log.txt`** if startup fails. Report your device, exact firmware version,
resolution and steps to reproduce, and attach the log. Test menu navigation, gameplay,
audio, game speed, save/reload, suspend/resume and clean exit. Keep purchased game files private.

## Licenses

The original port and host use the MIT license; their separate notices and the gptokeyb
GPL license are in `gunslugs3/licenses/`. Upstream copyright notices remain intact.
The game and screenshot retain Orangepixel's rights. Bundled replacement runtime libraries have separate licenses and LGPL source archives in that folder.
