#!/usr/bin/env sh
# Lightweight Gradle bootstrap used because this starter archive does not bundle
# Gradle's binary wrapper JAR. It downloads the pinned Gradle distribution once.
set -eu

GRADLE_VERSION="9.8.2"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
BOOTSTRAP_DIR="$GRADLE_USER_HOME/ragdoll-bootstrap"
GRADLE_HOME="$BOOTSTRAP_DIR/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
    mkdir -p "$BOOTSTRAP_DIR"
    ARCHIVE="$BOOTSTRAP_DIR/gradle-$GRADLE_VERSION-bin.zip"
    echo "Downloading Gradle $GRADLE_VERSION..."
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --retry 3 \
            "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" \
            --output "$ARCHIVE"
    elif command -v wget >/dev/null 2>&1; then
        wget -O "$ARCHIVE" \
            "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    else
        echo "Please install curl or wget to download Gradle." >&2
        exit 1
    fi
    unzip -q -o "$ARCHIVE" -d "$BOOTSTRAP_DIR"
    rm -f "$ARCHIVE"
fi

exec "$GRADLE_BIN" "$@"
