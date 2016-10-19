#!/bin/sh
set -e
cd ${HOME}
git config --global user.name "the-dagger"
git config --global user.email "harshithdwivedi@gmail.com"
ls
git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
ls
cp -r ${HOME}/${CIRCLE_PROJECT_REPONAME}/app/build/outputs/apk/ susi_android/apk/susi-debug.apk
cd apk
git add .
git commit -m "[Travis CI] Update Susi Apk"
git push origin apk --quiet > /dev/null
