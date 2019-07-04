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
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
	    /bin/rm -f *
	else
	    /bin/rm -f app-fdroid-debug.apk app-playStore-debug.apk app-playStore-release.apk app-fdroid-release.apk
	fi

    find ../app/build/outputs -type f -name '*.apk' -exec cp -v {} . \;
    find ../app/build/outputs -type f -name '*.aab' -exec cp -v {} . \;

	# Signing App
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Push to master branch detected, signing the app..."
		\cp app-playStore-release-unsigned.apk app-playStore-release-unaligned.apk
		jarsigner -verbose -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../exec/key.jks -storepass $STORE_PASS -keypass $KEY_PASS app-playStore-release-unaligned.apk $ALIAS
		${ANDROID_HOME}/build-tools/27.0.3/zipalign -vfp 4 app-playStore-release-unaligned.apk app-playStore-release.apk
	fi

	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		for file in app*; do
			cp $file susiai-master-${file%%}
		done
    	fi

    # Remove app- from the name of the files
    for file in *.apk;do
        if [[ "$file" == *"app-"* ]];then
            mv "${file}" "${file//app-/}"
        fi
    done

    # Remove playStore- from the name of the files
    for file in *.apk;do
        if [[ "$file" == *"playStore"* ]];then
            mv "${file}" "${file//playStore-/}"
        fi
    done

    # Append susiai-dev to the non master branch apk's
    for file in *.apk;do
        if [[ "$file" != *"master"* ]];then
            mv $file susiai-dev-${file%%}
        fi
    done

    # Remove unwanted apk files
    rm susiai-dev-fdroid-release-unsigned.apk
    rm susiai-dev-release-unaligned.apk
    rm susiai-master-release-unaligned.apk

    # Create a new branch that will contain only latest apk
	git checkout --orphan workaround

	# Add generated APK
	git add -A
	git commit -am "[Circle CI] Update Susi Apk"

    	# Delete current apk branch
	git branch -D apk
	# Rename current branch to apk
    	git branch -m apk

	# Force push to origin since histories are unrelated
	git push origin apk --force --quiet > /dev/null

	curl https://$APPETIZE_API_TOKEN@api.appetize.io/v1/apps/mbpprq4xj92c119j7nxdhttjm0 -H 'Content-Type: application/json' -d '{"url":"https://github.com/fossasia/susi_android/raw/apk/app-playStore-debug.apk", "note": "Update SUSI Preview"}'

	# Publish App to Play Store
	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
		echo "Publishing app to Play Store"
		gem install fastlane
		fastlane supply --aab susiai-master-release.aab --track alpha --json_key ../exec/fastlane.json --package_name $PACKAGE_NAME
	fi
fi
