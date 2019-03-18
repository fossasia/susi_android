#!/usr/bin/env bash

# Accept licenses
${ANDROID_HOME}/tools/bin/sdkmanager --licenses

# Install dependencies
./gradlew androidDependencies || true