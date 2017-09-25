#!/bin/bash
set -e

if [[ $CIRCLE_BRANCH != pull* ]]
then
	export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}

	git config --global user.name "the-dagger"
	git config --global user.email "harshithdwivedi@gmail.com"

	git clone --quiet --branch=apk https://the-dagger:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
	ls
	cp -r ${HOME}/${CIRCLE_PROJECT_REPONAME}/app/build/outputs/apk/app-debug.apk apk/susi-debug.apk
	cp -r ${HOME}/${CIRCLE_PROJECT_REPONAME}/app/build/outputs/apk/app-release-unsigned.apk apk/susi-release.apk
	cd apk

	# Signing App
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Push to master branch detected, signing the app..."
		cp susi-release.apk susi-release-unaligned.apk
		jarsigner -verbose -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../exec/key.jks -storepass $STORE_PASS -keypass $KEY_PASS susi-release-unaligned.apk $ALIAS
		${ANDROID_HOME}/build-tools/25.0.2/zipalign -v -p 4 susi-release-unaligned.apk susi-release-signed.apk
	fi

	git checkout --orphan workaround
	git add -A

	git commit -am "[Circle CI] Update Susi Apk"

	git branch -D apk
	git branch -m apk

	git push origin apk --force --quiet > /dev/null

	curl https://$APPETIZE_API_TOKEN@api.appetize.io/v1/apps/mbpprq4xj92c119j7nxdhttjm0 -H 'Content-Type: application/json' -d '{"url":"https://github.com/fossasia/susi_android/raw/apk/susi-debug.apk", "note": "Update SUSI Preview"}'

	# Publish App to Play Store
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Publishing app to Play Store"
		gem install fastlane
		fastlane supply --apk susi-release-signed.apk --track alpha --json_key ../exec/fastlane.json --package_name $PACKAGE_NAME
	fi
fi

