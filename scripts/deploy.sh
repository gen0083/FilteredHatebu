#!/bin/bash

# to test
echo $TRAVIS_PULL_REQUEST
git --version

pwd
VERSION=`./gradlew bumpVersion | grep -E "^v\d+\.\d+\.\d+"`
echo "$VERSION"
# ./gradlew :app:publishProdRelease
git add --all
git commit -m "Released $VERSION by CI"
git tag "$VERSION"
git push origin master
