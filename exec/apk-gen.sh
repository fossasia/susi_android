#!/bin/bash
set -e

if [[ $CIRCLE_BRANCH != pull* ]]
then
	export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}
	export DEPLOY_BRANCH=${DEPLOY_BRANCH:-development}

	git config --global user.name "Travis CI"
	git config --global user.email "noreply+travis@fossasia.org"

	./gradlew bundlePlayStoreRelease

	git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/susi_android apk > /dev/null
	ls
	cd apk

	if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
	    /bin/rm -f *
	else
	    /bin/rm -f susiai-dev-*
	fi

    find ../app/build/outputs -type f -name '*.apk' -exec cp -v {} . \;
    find ../app/build/outputs -type f -name '*.aab' -exec cp -v {} . \;

    for file in app*; do
        if [ "$CIRCLE_BRANCH" == "$PUBLISH_BRANCH" ]; then
            if [[ ${file} =~ ".aab" ]]; then
                mv $file susiai-master-${file}
            else
                mv $file susiai-master-${file:4}
            fi

        elif [ "$CIRCLE_BRANCH" == "$DEPLOY_BRANCH" ]; then
            if [[ ${file} =~ ".aab" ]]; then
                mv $file susiai-dev-${file}
            else
                mv $file susiai-dev-${file:4}
            fi

        fi
    done

    # Create a new branch that will contain only latest apk
	git checkout --orphan workaround

	# Add generated APK
	git add -A
	git commit -am "[Circle CI] Update Susi Apk ($(date +%Y-%m-%d.%H:%M:%S))"

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
		cd ..
		gem install fastlane
		fastlane supply --aab ./apk/susiai-master-app.aab --skip_upload_apk true --track alpha --json_key ./exec/fastlane.json --package_name $PACKAGE_NAME
	fi
fi
