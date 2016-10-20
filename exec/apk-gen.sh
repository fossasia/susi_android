#!/bin/sh
set -e

git config --global user.name "the-dagger"
git config --global user.email "harshithdwivedi@gmail.com"

git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
ls
cp -r ${HOME}/${CIRCLE_PROJECT_REPONAME}/app/build/outputs/apk/app-debug.apk apk/susi-debug.apk
cp -r ${HOME}/${CIRCLE_PROJECT_REPONAME}/app/build/outputs/apk/app-release-unsigned.apk apk/susi-release.apk
cd apk
git add .
git commit -m "[Circle CI] Update Susi Apk"
git push origin apk --quiet > /dev/null
