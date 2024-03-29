package com.stepstone.xrunner

import android.os.Bundle
import android.util.Log
import androidx.test.internal.events.client.TestEventClient
import androidx.test.internal.runner.RunnerArgs
import androidx.test.internal.runner.TestRequestBuilder
import androidx.test.runner.AndroidJUnitRunner
import com.stepstone.xrunner.internal.ARGUMENT_CLASS
import com.stepstone.xrunner.internal.ARGUMENT_LIST_TESTS_FOR_ORCHESTRATOR
import com.stepstone.xrunner.internal.ARGUMENT_NOT_CLASS
import com.stepstone.xrunner.internal.ARGUMENT_NOT_PACKAGE
import com.stepstone.xrunner.internal.ARGUMENT_ORCHESTRATOR_DISCOVERY_SERVICE
import com.stepstone.xrunner.internal.ARGUMENT_PACKAGE
import com.stepstone.xrunner.internal.ARGUMENT_TARGET_PROCESS
import com.stepstone.xrunner.internal.OrchestratorListenerWrapper
import com.stepstone.xrunner.internal.createXRunnerTestDescription
import com.stepstone.xrunner.internal.getClassArgument
import com.stepstone.xrunner.internal.getMethodTestArgumentsFromDescription
import com.stepstone.xrunner.internal.getXRunnerClassArgument
import com.stepstone.xrunner.internal.getXRunnerCountArgument
import com.stepstone.xrunner.internal.getXRunnerNotClassArgument
import com.stepstone.xrunner.internal.getXRunnerNotPackageArgument
import com.stepstone.xrunner.internal.getXRunnerPackageArgument
import com.stepstone.xrunner.internal.isXRunnerTestExecution
import com.stepstone.xrunner.internal.shouldUseXRunner
import com.stepstone.xrunner.internal.unwrapTestMethodFromXRunner
import de.jodamob.reflect.SuperReflect
import org.junit.runner.Description
import org.junit.runner.notification.RunListener

open class AndroidJUnitXRunner : AndroidJUnitRunner() {

    companion object {
        private const val TAG = "XRunner"

        private const val RUNNER_FIELD_TEST_EVENT_CLIENT = "testEventClient"
        private const val RUNNER_FIELD_RUNNER_ARGS = "runnerArgs"
        private const val RUNNER_METHOD_CREATE_TEST_REQUEST_BUILDER = "createTestRequestBuilder"
        private const val RUNNER_ARGS_FIELD_LISTENERS = "listeners"
    }

    var originalTestToRun: String? = null

    private lateinit var originalBundle: Bundle

    private lateinit var modifiedBundle: Bundle

    override fun onCreate(bundle: Bundle) {
        this.originalBundle = bundle
        modifiedBundle = Bundle(bundle).apply {
            when {
                shouldUpdateTestSuiteForXRunner() -> setTestArgumentsForXRunnerTestList()
                isXRunnerTestExecution(bundle) -> {
                    originalTestToRun = getClassArgument(bundle)
                    updateTestArgumentForXRunnerTestExecution()
                }
                else -> Log.i(TAG, "Regular run so XRunner not needed. Nothing to see here...")
            }
        }
        super.onCreate(modifiedBundle)
    }

    override fun onStart() {
        val testEventClient = SuperReflect.on(this).get<TestEventClient>(RUNNER_FIELD_TEST_EVENT_CLIENT)

        if (shouldUpdateTestSuiteForXRunner()) {
            val runCount = getXRunnerCountArgument(modifiedBundle)
            addXRunnerTestsWithOrchestrator(runCount, modifiedBundle, testEventClient)
        } else if (isXRunnerTestExecution(originalBundle)) {
            updateListenersBeforeXRunnerTestExecution(testEventClient)
        }
        super.onStart()
    }

    fun getTestDescriptionFromBundle(bundle: Bundle): Description {
        val runnerArgs = getRunnerArgsWithReflection()
        val builder: TestRequestBuilder = SuperReflect.on(this).call(RUNNER_METHOD_CREATE_TEST_REQUEST_BUILDER, this, bundle).get()
        builder.addPathsToScan(runnerArgs.classpathToScan)
        if (runnerArgs.classpathToScan.isEmpty()) {
            builder.addPathToScan(context.packageCodePath)
        }
        builder.addFromRunnerArgs(runnerArgs)
        val request = builder.build()
        return request.runner.description
    }

    @Suppress("DEPRECATION")
    private fun isPrimaryInstrumentationProcess(): Boolean = isPrimaryInstrProcess(originalBundle.getString(ARGUMENT_TARGET_PROCESS))

    private fun addXRunnerTestsWithOrchestrator(runCount: Int, bundle: Bundle, testEventClient: TestEventClient) {
        val testDescriptionFromBundle = getTestDescriptionFromBundle(bundle)
        val testsToRerun = getMethodTestArgumentsFromDescription(testDescriptionFromBundle)

        Log.i(TAG, "About to run tests $runCount times with XRunner. Rerunning the following tests: $testsToRerun")
        testsToRerun
            .asSequence()
            .filter { it.hasTestMethod() }
            .flatMap { testArg -> (1 until runCount).map { createXRunnerTestDescription(testArg, it) }.asSequence() }
            .forEach {
                Log.d(TAG, "Adding '$it' to test suite")
                testEventClient.addTests(it)
            }
    }

    private fun shouldUpdateTestSuiteForXRunner(): Boolean {
        val listTestsForOrchestrator = (originalBundle.getString(ARGUMENT_LIST_TESTS_FOR_ORCHESTRATOR, false.toString())!!).toBoolean()
        val testDiscoveryForOrchestratorV2Provided = !originalBundle.getString(ARGUMENT_ORCHESTRATOR_DISCOVERY_SERVICE).isNullOrEmpty()
        return isPrimaryInstrumentationProcess() &&
            (testDiscoveryForOrchestratorV2Provided || listTestsForOrchestrator) &&
            shouldUseXRunner(originalBundle)
    }

    private fun updateListenersBeforeXRunnerTestExecution(testEventClient: TestEventClient) {
        val runnerArgs = getRunnerArgsWithReflection()
        SuperReflect.on(runnerArgs).set(
            RUNNER_ARGS_FIELD_LISTENERS,
            mutableListOf<RunListener>(
                OrchestratorListenerWrapper(
                    testEventClient.runListener!!,
                    originalTestToRun
                )
            )
        )
        /* We need to set test event client to no-op as otherwise it would be added in AndroidJUnitRunner#addListeners()
         * and it would duplicate what SCXRunnerOrchestratorListenerWrapper is already doing.
         */
        SuperReflect.on(this).set(RUNNER_FIELD_TEST_EVENT_CLIENT, TestEventClient.NO_OP_CLIENT)
    }

    private fun getRunnerArgsWithReflection() = SuperReflect.on(this).get<RunnerArgs>(RUNNER_FIELD_RUNNER_ARGS)

    private fun Bundle.setTestArgumentsForXRunnerTestList() {
        apply {
            remove(ARGUMENT_CLASS)
            remove(ARGUMENT_NOT_CLASS)
            remove(ARGUMENT_PACKAGE)
            remove(ARGUMENT_NOT_PACKAGE)
            putStringIfValueNotEmpty(ARGUMENT_CLASS, getXRunnerClassArgument(this))
            putStringIfValueNotEmpty(ARGUMENT_NOT_CLASS, getXRunnerNotClassArgument(this))
            putStringIfValueNotEmpty(ARGUMENT_PACKAGE, getXRunnerPackageArgument(this))
            putStringIfValueNotEmpty(ARGUMENT_NOT_PACKAGE, getXRunnerNotPackageArgument(this))
        }
    }

    private fun Bundle.updateTestArgumentForXRunnerTestExecution() {
        val unwrappedTestMethodFromXRunner = unwrapTestMethodFromXRunner(originalTestToRun!!)
        Log.d(TAG, "Replacing class argument $originalTestToRun with $unwrappedTestMethodFromXRunner")
        putString(ARGUMENT_CLASS, unwrappedTestMethodFromXRunner)
    }

    private fun Bundle.putStringIfValueNotEmpty(key: String, value: String?) {
        if (!value.isNullOrEmpty()) {
            putString(key, value)
        }
    }
}
