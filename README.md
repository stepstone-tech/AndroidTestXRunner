# Android Test XRunner

This library allows you to run Android UI tests multiple times in a single instrumentation execution. Written in Kotlin.

# Why use it?
Let's face it - instrumentation tests can be flaky. Even if your brand new test passes the first time you run it on a device, it doesn't mean that it won't fail if you run it again. Therefore, it's a good practice to try it at least a couple of times just to be sure.

Common approaches are to either run the test over and over again via e.g. consecutive `./gradlew connectedDebugAndroidTest` calls or to copy-paste that test with different names and run the test suite once. Neither one of these is perfect and that's where Android Test XRunner comes into play.

It heavily depends on [Android Test Orchestrator](https://medium.com/stepstone-tech/android-test-orchestrator-unmasked-83b8879928fa), which it "tricks" to execute same tests multiple times.

# Getting started

## Setup
### 1. Add a Gradle dependency in your `build.gradle`

```groovy
implementation 'com.stepstone.xrunner:xrunner-library:2.0.0'
```

*NOTE:* not on AndroidX yet? See *Compatibility* section at the bottom.

### 2. Use AndroidJUnitXRunner instead of your runner

In your app's `build.gradle` file add the following:

```groovy
android {
    
    defaultConfig {
        // ...
        testInstrumentationRunner "com.stepstone.xrunner.AndroidJUnitXRunner"
    }
}
```

### 3. Enable Android Test Orchestrator

XRunner relies on Android Test Orchestrator therefore Android Test Orchestrator must be enabled. To do so in your app's `build.gradle` file add the following:
```groovy
android {
    
    testOptions {
        execution 'ANDROIDX_TEST_ORCHESTRATOR'
    }
}
    
dependencies {
    androidTestUtil 'androidx.test:orchestrator:1.1.0'
}
```

## Hot to run tests?
XRunner relies on custom instrumentation arguments. To use it you need to specify at least one of `xrunnerClass`, `xrunnerNotClass`, `xrunnerPackage` or `xrunnerNotPackage`.
They are very similar to [AndroidJUnitRunner](https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner)'s `class`, `notClass`, `package` or `notPackage`. For more details see [this article](https://medium.com/stepstone-tech/exploring-androidjunitrunner-filtering-options-df26d30b4f60).

Here are some samples:

### Run single test 10 times
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest#clicking_fab_should_show_dummy_text
```

### Run all tests in a test class 3 times each
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3
```

### Run all tests in a test class 3 times each excluding one of the tests
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.xrunnerClass=com.stepstone.xrunner.sample.MainActivityTest -Pandroid.testInstrumentationRunnerArguments.xrunnerNotClass=com.stepstone.xrunner.sample.MainActivityTest#clicking_fab_should_show_dummy_text -Pandroid.testInstrumentationRunnerArguments.xrunnerCount=3
```

### Run all tests in a package
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.xrunnerPackage=com.stepstone.xrunner.sample.dummy
```

### Run all tests in a package excluding one of the subpackages
```bash
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.xrunnerPackage=com.stepstone.xrunner.sample -Pandroid.testInstrumentationRunnerArguments.xrunnerNotPackage=com.stepstone.xrunner.sample.dummy
```

# Compatibility

| XRunner version                                                          | Test runner version                     |
|:------------------------------------------------------------------------:|:---------------------------------------:|
| [2.0.0](https://github.com/stepstone-tech/AndroidTestXRunner/tree/2.0.0) | `androidx.test:runner:1.1.0`            |
| [1.0.0](https://github.com/stepstone-tech/AndroidTestXRunner/tree/1.0.0) | `com.android.support.test:runner:1.0.2` |
