#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" == 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ] || [ -n "$TRAVIS_TAG" ]; then
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi
