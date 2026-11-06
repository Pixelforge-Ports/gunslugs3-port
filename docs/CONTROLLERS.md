# Controller support

The launcher starts PortMaster's gptokeyb2 in keyboard-mapping mode using
`gunslugs3.ini`. The D-pad and left stick send arrow keys. The configured
gptokeyb buttons send: `a` → A, `b` → Z, `x` → X, and `y` → Up. L1 sends Up,
R1 sends X, L2 sends Z, R2 sends Tab, Start sends Enter, and Back sends Escape.
Start + Select remains PortMaster's force-close shortcut.

The GOG Controls screen lists arrow keys, Shoot X, Cancel Escape, Swap Weapon Z,
Use R, and Pause Tab. This file records the requested gptokeyb mappings exactly.

The launcher's `HOTKEY=back` setting keeps the PortMaster exit hotkey enabled.
Do not add gptokeyb2's `-x` mode: this port uses keyboard input rather than the
game's native controller path.
