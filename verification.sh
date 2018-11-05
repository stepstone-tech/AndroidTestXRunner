#!/bin/bash

source ./_functions.sh

./gradlew clean

./gradlew ktlint
echo "ktlint OK!"

#Default run
./gradlew connectedDebugAndroidTest --info || true
verifyJUnitReport 6 1 1
echo "Default run OK!"

#Run method
./gradlew connectedDebugAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest#clicking_fab_should_show_dummy_text || true
verifyJUnitReport 10 0 0
echo "Single test method run OK!"

#Run class
./gradlew connectedDebugAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3 || true
verifyJUnitReport 12 3 3
echo "Test class run OK!"

#Run class without some tests in it
./gradlew connectedDebugAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest -Pandroid.testInstrumentationRunnerArguments.xrunnerNotClass=com.stepstone.xrunner.sample.MainActivityTest#clicking_fab_should_show_dummy_text -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3 || true
verifyJUnitReport 9 3 3
echo "Test class with exclusions run OK!"

#Run package
./gradlew connectedDebugAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.xrunnerPackage=com.stepstone.xrunner.sample.dummy -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3 || true
verifyJUnitReport 3 0 0
echo "Test package run OK!"

#Run package without some subpackages
./gradlew connectedDebugAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.xrunnerPackage=com.stepstone.xrunner.sample -Pandroid.testInstrumentationRunnerArguments.xrunnerNotPackage=com.stepstone.xrunner.sample.dummy -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3 || true
verifyJUnitReport 15 3 3
echo "Test package with exclusions run OK!"

./gradlew clean assembleRelease publishToMavenLocal
