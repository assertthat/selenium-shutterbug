#!/usr/bin/env bash
if [ -n "$TRAVIS_TAG" ]; then
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi
