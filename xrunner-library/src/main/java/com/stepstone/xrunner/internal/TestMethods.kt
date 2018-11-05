package com.stepstone.xrunner.internal

import org.junit.runner.Description

private const val XRUNNER_TEST_METHOD_SUFFIX = "_XRUNNER_RUN_"

fun isXRunnerTest(testClassName: String?): Boolean = testClassName?.contains(XRUNNER_TEST_METHOD_SUFFIX) == true

fun createXRunnerTestDescription(testArg: TestArgument, index: Int): Description =
    Description.createTestDescription(testArg.testClassName, testArg.methodName + XRUNNER_TEST_METHOD_SUFFIX + index)

fun unwrapTestMethodFromXRunner(classArgumentWithXRunnerSuffix: String): String {
    val indexOfXRunnerSuffix = classArgumentWithXRunnerSuffix.indexOf(XRUNNER_TEST_METHOD_SUFFIX)
    if (indexOfXRunnerSuffix == -1) throw IllegalStateException("Unable to unwrap class argument with XRunner! Class argument: $classArgumentWithXRunnerSuffix")

    return classArgumentWithXRunnerSuffix.substring(0, indexOfXRunnerSuffix)
}
