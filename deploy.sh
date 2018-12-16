#!/usr/bin/env bash

set -ex

git checkout gh-pages
git merge --no-edit master

# Build Clojurescript
lein clean
lein cljsbuild once min

# Copy built files into root
cp resources/public/index.html .
mkdir -p js/compiled
cp resources/public/js/compiled/app.js js/compiled
cp -R resources/public/css .
cp -R resources/public/vendor .

git add index.html js css vendor
git commit -m "Deploy"
git push
git checkout master
