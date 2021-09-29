package com.stepstone.xrunner.internal

import androidx.test.internal.events.client.OrchestratedInstrumentationListener
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

/**
 * This is a wrapper for the original [OrchestratedInstrumentationListener] set in [androidx.test.runner.AndroidJUnitRunner].
 * It's purpose is to report to Android Test Orchestrator correct XRunner test methods e.g.
 * if [com.stepstone.xrunner.AndroidJUnitXRunner] was originally triggered to run "com.android.foo.FooTest#testFoo_XRUNNER_RUN_1"
 * this class would make sure that [OrchestratedInstrumentationListener] would get a test description with "_XRUNNER_RUN_1" suffix.
 * This is because [com.stepstone.xrunner.AndroidJUnitXRunner] removes this suffix when running the test.
 */
class OrchestratorListenerWrapper(
    private val orchestratorListener: RunListener,
    private val originalTestToRun: String?
) : RunListener() {

    override fun testRunStarted(description: Description?) {
        orchestratorListener.testRunStarted(description)
    }

    override fun testStarted(description: Description?) {
        val newDescription = getXRunnerDescription(description)
        orchestratorListener.testStarted(newDescription)
    }

    override fun testAssumptionFailure(failure: Failure?) {
        orchestratorListener.testAssumptionFailure(failure)
    }

    override fun testRunFinished(result: Result?) {
        orchestratorListener.testRunFinished(result)
    }

    override fun testFailure(failure: Failure?) {
        val newDescription = getXRunnerDescription(failure?.description)
        orchestratorListener.testFailure(Failure(newDescription, failure?.exception))
    }

    override fun testFinished(description: Description?) {
        val newDescription = getXRunnerDescription(description)
        orchestratorListener.testFinished(newDescription)
    }

    override fun testIgnored(description: Description?) {
        val newDescription = getXRunnerDescription(description)
        orchestratorListener.testIgnored(newDescription)
    }

    private fun getXRunnerDescription(description: Description?): Description? {
        return if (isXRunnerTest(originalTestToRun)) {
            val testArgForOriginalTestToRun = parseTestClass(originalTestToRun)!!
            Description.createTestDescription(testArgForOriginalTestToRun.testClassName, testArgForOriginalTestToRun.methodName)
        } else {
            description
        }
    }
}
