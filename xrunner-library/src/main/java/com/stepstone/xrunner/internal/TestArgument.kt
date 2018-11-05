package com.stepstone.xrunner.internal

import org.junit.runner.Description

/**
 * Encapsulates a test class and optional method.
 * Copied from [android.support.test.internal.runner.RunnerArgs] where it's private or package-private.
 */
data class TestArgument(val testClassName: String, val methodName: String? = null) {

    fun hasTestMethod(): Boolean = methodName != null
}

/**
 * Used to separate multiple fully-qualified test case class names
 */
private const val CLASS_SEPARATOR = ","

/**
 * Used to separate fully-qualified test case class name, and one of its methods
 */
private const val METHOD_SEPARATOR = '#'

/**
 * Parse an individual test class and optionally method from given string.
 *
 *
 * Expected format: com.TestClass1[#method1]
 */
fun parseTestClass(name: String?): TestArgument? {
    if (name.isNullOrEmpty()) {
        return null
    }
    val methodSeparatorIndex = name!!.indexOf(METHOD_SEPARATOR)
    return if (methodSeparatorIndex > 0) {
        val testMethodName = name.substring(methodSeparatorIndex + 1)
        val testClassName = name.substring(0, methodSeparatorIndex)
        TestArgument(testClassName, testMethodName)
    } else {
        TestArgument(name)
    }
}

fun getMethodTestArgumentsFromDescription(description: Description): List<TestArgument> {
    if (description.isEmpty) {
        return emptyList()
    }

    return if (description.isTest) {
        listOf(TestArgument(description.className, description.methodName))
    } else {
        val list = mutableListOf<TestArgument>()
        for (child in description.children) {
            list.addAll(getMethodTestArgumentsFromDescription(child))
        }
        list.toList()
    }
}
