#!/bin/sh
set -e

git config --global user.name "the-dagger"
git config --global user.email "harshithdwivedi@gmail.com"

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/susi_android" ]; then
    echo "No push. Exiting."
    exit 0
fi

git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
cp /home/travis/build/fossasia/susi_android/app/build/outputs/apk/app-debug.apk apk/susi-debug.apk
cp /home/travis/build/fossasia/susi_android/app/build/outputs/apk/app-release-unsigned.apk apk/susi-release.apk
cd apk
git add .
git commit -m "[Travis CI] Update Susi Apk"
git push origin apk --quiet > /dev/null
