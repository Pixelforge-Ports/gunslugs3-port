#!/bin/bash
# shellcheck source-path=SCRIPTDIR
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.."
# shellcheck source=../package/gunslugs3/display.inc
source package/gunslugs3/display.inc
GAMEDIR="$(mktemp -d)"
trap 'rmdir "$GAMEDIR"' EXIT
for size in 640x480 720x480 720x720 1024x768 1280x720 960x544 320x240; do
    DISPLAY_WIDTH="${size%x*}" DISPLAY_HEIGHT="${size#*x}"
    GUNSLUGS3_RESOLUTION=auto
    gunslugs3_display_setup
    [[ "${display_env[0]}" == "WESTON_HEADLESS_WIDTH=$DISPLAY_WIDTH" ]]
    [[ "${display_env[1]}" == "WESTON_HEADLESS_HEIGHT=$DISPLAY_HEIGHT" ]]
    [[ "${display_java[0]}" == "-Dgunslugs3.width=$DISPLAY_WIDTH" ]]
    [[ "${display_java[1]}" == "-Dgunslugs3.height=$DISPLAY_HEIGHT" ]]
done
DISPLAY_WIDTH=0 DISPLAY_HEIGHT=0
gunslugs3_display_setup
[[ "${#display_env[@]}" == 0 && "${#display_java[@]}" == 0 ]]
for bad in 0x480 640x0 100x100 9999x720 640x480oops '640x480;exit' 640X480; do
    GUNSLUGS3_RESOLUTION="$bad"
    if gunslugs3_display_setup; then echo "Accepted invalid size: $bad"; exit 1; fi
done
GUNSLUGS3_RESOLUTION=0720x0480
gunslugs3_display_setup
[[ "${display_java[0]}" == '-Dgunslugs3.width=720' ]]
[[ "${display_java[1]}" == '-Dgunslugs3.height=480' ]]
printf '720x720\r\n' > "$GAMEDIR/resolution.txt"
GUNSLUGS3_RESOLUTION=""
gunslugs3_display_setup
[[ "${display_java[1]}" == '-Dgunslugs3.height=720' ]]
GUNSLUGS3_RESOLUTION=1280x720
gunslugs3_display_setup
[[ "${display_java[0]}" == '-Dgunslugs3.width=1280' ]]
rm -- "$GAMEDIR/resolution.txt"
echo 'DISPLAY_CHECKS_OK: sizes, override precedence, CRLF, invalid inputs, automatic fallback'
