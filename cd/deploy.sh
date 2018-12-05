#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    ./gradlew uploadArchives -Psigning.keyId=$GPG_KEY_NAME -Psigning.password=$GPG_PASSPHRASE
fi