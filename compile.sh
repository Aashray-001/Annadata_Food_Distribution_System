#!/bin/sh
# Compile the Annadata web edition (macOS / Linux)
set -e
cd "$(dirname "$0")"
mkdir -p web/out
find src web/src -name '*.java' > web/sources-web.txt
javac -cp "lib/*" -d web/out @web/sources-web.txt
echo "[SUCCESS] Compiled. Start it with ./run.sh"
