# Android App for Susi

[![Build Status](https://travis-ci.org/fossasia/susi_android.svg?branch=master)](https://travis-ci.org/fossasia/susi_android)
[![Gitter](https://badges.gitter.im/fossasia/susi_android.svg)](https://gitter.im/fossasia/susi_android?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

The main feature of the app is to provide a conversational interface to provide intelligent answers using the loklak/AskSusi infrastructure. The app also offers login functionalities to connect to other services and stored personal data. Additionally the application uses data provided by the user's phone to improve Susi answers. Geolocation information for example helps to offer better answers related to questions about "things nearby".

## Roadmap

First steps are to implement the basic chat functionalities of Susi.

## Android App Development Set up

Please find info about the set up of the Android app in your development environment [here](https://github.com/fossasia/susi_android/tree/master/docs/Android_App_Setup.md).

## Communication

Please join our mailing list to discuss questions regarding the project: https://groups.google.com/forum/#!forum/loklak

Our chat channel is on gitter here: https://gitter.im/fossasia/susi_android

## Development

A native Android app.

## Branch Policy

Note: For the initialization period all commits go directly to the master branch. In the next stages we follow the branch policy as below:

We have the following branches
 * **development**
	 All development goes on in this branch. If you're making a contribution,
	 you are supposed to make a pull request to _development_.
	 PRs to gh-pages must pass a build check and a unit-test check on Travis
 * **master**
   This contains shipped code. After significant features/bugfixes are accumulated on development, we make a version update, and make a release.

## Code practices

Please help us follow the best practice to make it easy for the reviewer as well as the contributor. We want to focus on the code quality more than on managing pull request ethics. 

 * Single commit per pull request
 * Reference the issue numbers in the commit message. Follow the pattern ``` Fixes #<issue number> <commit message>```
 * The pull request will not get merged until and unless the commits are squashed. In case there are multiple commits on the PR, the commit author needs to squash them and not the maintainers cherrypicking and merging squashes.
 * If the PR is related to any front end change, please attach relevant screenshots in the pull request description.

## License

This project is currently licensed under the Apache License Version 2.0. A copy of LICENSE.md should be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.
