#!/bin/sh
set -e

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-master}

if [ "$CIRCLE_PROJECT_USERNAME" != "fossasia" -o "$CIRCLE_BRANCH" != "$DEPLOY_BRANCH" ]; then
    echo "We decrypt key only for pushes to the master branch and not PRs. So, skip."
    exit 0
fi

openssl aes-256-cbc -d -in ./exec/secrets.tar.enc -out ./exec/secrets.tar -k $ENCRYPT_KEY
tar xvf ./exec/secrets.tar -C exec/
