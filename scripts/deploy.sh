#!/bin/bash

# to test
echo $TRAVIS_PULL_REQUEST
git --version

pwd
VERSION=`./gradlew bumpVersion | grep -E "^v\d+\.\d+\.\d+"`
echo "$VERSION"
./gradlew :app:publishProdReleaseBundle
git add --all
git commit -m "Released $VERSION"
git tag "$VERSION"
