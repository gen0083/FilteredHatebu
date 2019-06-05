#!/bin/bash

VERSION=`./gradlew bumpVersion | grep -E "^v\d+\.\d+\.\d+"`
echo "$VERSION"
./gradlew :app:publishProdReleaseBundle
git add --all
git commit -m "Released $VERSION"
git tag "$VERSION"
