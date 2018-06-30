#!/bin/bash

# to test
echo $TRAVIS_PULL_REQUEST
git --version

pwd
./gradlew bumpVersion
./gradlew :app:assembleProdRelease
./gradlew :app:publishProdRelease
