#!/bin/bash
set -e

if [[ $CIRCLE_BRANCH != pull* ]]
then
	export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}

	git config --global user.name "Travis CI"
	git config --global user.email "noreply+travis@fossasia.org"

	git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
	ls
	cd apk
	/bin/rm -f *
	\cp -r ../app/build/outputs/apk/*/**.apk .
	\cp -r ../app/build/outputs/apk/debug/output.json debug-output.json
	\cp -r ../app/build/outputs/apk/release/output.json release-output.json

	# Signing App
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Push to master branch detected, signing the app..."
		\cp app-fdroid-release-unsigned.apk app-fdroid-release-unaligned.apk
		\cp app-playStore-release-unsigned.apk app-playStore-release-unaligned.apk
		jarsigner -verbose -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../exec/key.jks -storepass $STORE_PASS -keypass $KEY_PASS app-release-unaligned.apk $ALIAS
		${ANDROID_HOME}/build-tools/27.0.3/zipalign -vfp 4 app-release-unaligned.apk app-release.apk
	fi

	git checkout --orphan workaround
	git add -A

	git commit -am "[Circle CI] Update Susi Apk"

	git branch -D apk
	git branch -m apk

	git push origin apk --force --quiet > /dev/null

	curl https://$APPETIZE_API_TOKEN@api.appetize.io/v1/apps/mbpprq4xj92c119j7nxdhttjm0 -H 'Content-Type: application/json' -d '{"url":"https://github.com/fossasia/susi_android/raw/apk/app-debug.apk", "note": "Update SUSI Preview"}'

	# Publish App to Play Store
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Publishing app to Play Store"
		gem install fastlane
		fastlane supply --apk app-release.apk --track alpha --json_key ../exec/fastlane.json --package_name $PACKAGE_NAME
	fi
fi
