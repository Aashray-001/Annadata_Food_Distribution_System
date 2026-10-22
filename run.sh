#!/bin/sh
# Start the Annadata web edition (macOS / Linux)
cd "$(dirname "$0")"
[ -f web/out/web/WebServer.class ] || ./compile.sh
echo "Open http://localhost:${PORT:-8080} in your browser. Press Ctrl+C to stop."
java -cp "web/out:lib/*" web.WebServer
