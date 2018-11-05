# Android Test XRunner

This library allows you to run Android UI tests multiple times in a single instrumentation execution. Written in Kotlin.

# Getting started

## Setup
### 1. Add a Gradle dependency in your `build.gradle`

```groovy
implementation 'com.stepstone.xrunner:xrunner-library:1.0.0'
```

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
        execution 'ANDROID_TEST_ORCHESTRATOR'
    }
}
    
dependencies {
    androidTestUtil 'com.android.support.test:orchestrator:1.0.2'
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
