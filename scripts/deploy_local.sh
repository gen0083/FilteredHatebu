#!/bin/bash -eu

./gradlew bumpVersion
VERSION=`./gradlew dumpVersionName | grep -e "^v\d+\.\d+\.\d+"`
echo "$VERSION"
./gradlew :app:publishProdReleaseBundle
git add --all
git commit -m "Released $VERSION"
git tag "$VERSION"
