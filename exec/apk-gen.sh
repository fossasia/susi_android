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
	\cp -r ../app/build/outputs/apk/fdroid/*/**.apk .
	\cp -r ../app/build/outputs/apk/playStore/*/**.apk .
	\cp -r ../app/build/outputs/apk/fdroid/debug/output.json fdroidDebug-output.json
	\cp -r ../app/build/outputs/apk/fdroid/release/output.json fdroidRelease-output.json
	\cp -r ../app/build/outputs/apk/playStore/debug/output.json playStoreDebug-output.json
	\cp -r ../app/build/outputs/apk/playStore/release/output.json playStoreRelease-output.json

	# Signing App
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Push to master branch detected, signing the app..."
		\cp app-playStore-release-unsigned.apk app-playStore-release-unaligned.apk
		jarsigner -verbose -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../exec/key.jks -storepass $STORE_PASS -keypass $KEY_PASS app-playStore-release-unaligned.apk $ALIAS
		${ANDROID_HOME}/build-tools/27.0.3/zipalign -vfp 4 app-playStore-release-unaligned.apk app-playStore-release.apk
	fi

	git checkout --orphan workaround
	git add -A

	git commit -am "[Circle CI] Update Susi Apk"

	git branch -D apk
	git branch -m apk

	git push origin apk --force --quiet > /dev/null

	curl https://$APPETIZE_API_TOKEN@api.appetize.io/v1/apps/mbpprq4xj92c119j7nxdhttjm0 -H 'Content-Type: application/json' -d '{"url":"https://github.com/fossasia/susi_android/raw/apk/app-playStore-debug.apk", "note": "Update SUSI Preview"}'

	# Publish App to Play Store
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Publishing app to Play Store"
		gem install fastlane
		fastlane supply --apk app-playStore-release.apk --track alpha --json_key ../exec/fastlane.json --package_name $PACKAGE_NAME
	fi
fi
