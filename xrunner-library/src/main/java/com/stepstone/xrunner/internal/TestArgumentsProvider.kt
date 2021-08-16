package com.stepstone.xrunner.internal

import android.os.Bundle
import kotlin.math.max

private const val XRUNNER_DEFAULT_COUNT = "10"

fun getClassArgument(bundle: Bundle): String? = bundle.getString(ARGUMENT_CLASS)

fun isXRunnerTestExecution(bundle: Bundle): Boolean = isXRunnerTest(getClassArgument(bundle))

fun getXRunnerCountArgument(bundle: Bundle): Int = max(
    bundle.getString(
        ARGUMENT_XRUNNER_COUNT,
        XRUNNER_DEFAULT_COUNT
    ).toInt(),
    0
)

fun shouldUseXRunner(bundle: Bundle): Boolean = !getXRunnerClassArgument(bundle).isNullOrEmpty() ||
    !getXRunnerNotClassArgument(bundle).isNullOrEmpty() ||
    !getXRunnerPackageArgument(bundle).isNullOrEmpty() ||
    !getXRunnerNotPackageArgument(bundle).isNullOrEmpty()

fun getXRunnerClassArgument(bundle: Bundle): String? = bundle.getString(ARGUMENT_XRUNNER_CLASS)

fun getXRunnerNotClassArgument(bundle: Bundle): String? = bundle.getString(ARGUMENT_XRUNNER_NOT_CLASS)

fun getXRunnerPackageArgument(bundle: Bundle): String? = bundle.getString(ARGUMENT_XRUNNER_PACKAGE)

fun getXRunnerNotPackageArgument(bundle: Bundle): String? = bundle.getString(ARGUMENT_XRUNNER_NOT_PACKAGE)
