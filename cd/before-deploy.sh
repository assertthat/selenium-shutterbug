#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
     openssl aes-256-cbc -K $encrypted_ff4bd488efde_key -iv $encrypted_ff4bd488efde_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc -d
fi
