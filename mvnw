#!/bin/sh
# Lightweight wrapper to run Maven using IntelliJ's bundled Maven or system Maven
INTEL_MVN="/Applications/IntelliJ IDEA CE.app/Contents/plugins/maven/lib/maven3/bin/mvn"

if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
elif [ -x "$INTEL_MVN" ]; then
    exec "$INTEL_MVN" "$@"
else
    echo "Error: Maven not found."
    exit 1
fi
