#!/bin/sh
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/susi_android" ]; then
    echo "No push. Exiting."
    exit 0
fi

git clone --quiet --branch=apk https://<GITHUB_API_KEY>@github.com/fossasia/susi_android app > /dev/null
cp platforms/android/build/outputs/apk/android-debug.apk app/susi.apk
cd app
git add susi.apk
git commit -m "[Travis CI] Update Susi Test Apk"
git push origin apk --quiet > /dev/null