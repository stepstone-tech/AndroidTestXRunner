#!/bin/bash

set -o errexit

function verifyJUnitReport()
{
    local expected_tests_executed=${1}
    local expected_tests_failed=${2}
    local expected_tests_skipped=${3}

    local report_file=$(find app/build/outputs/androidTest-results/connected -type f)

    echo "Report file: ${report_file}"

    local testsExecuted=$(sed -ne "s/\<testsuite .*tests=[\"]\([^\"]*\)[\"].*/\1/p" "${report_file}")
    local testsFailed=$(sed -ne "s/\<testsuite .*failures=[\"]\([^\"]*\)[\"].*/\1/p" "${report_file}")
    local testsSkipped=$(sed -ne "s/\<testsuite .*skipped=[\"]\([^\"]*\)[\"].*/\1/p" "${report_file}")

    if [ "${expected_tests_executed}" !=  "${testsExecuted}" ] ; then
        echo "Incorrect number of executed tests. Expected ${expected_tests_executed} but got ${testsExecuted}"
        exit 1
    fi
    if [ "${expected_tests_failed}" !=  "${testsFailed}" ] ; then
        echo "Incorrect number of failed tests. Expected ${expected_tests_failed} but got ${testsFailed}"
        exit 1
    fi
    if [ "${expected_tests_skipped}" !=  "${testsSkipped}" ] ; then
        echo "Incorrect number of skipped tests. Expected ${expected_tests_skipped} but got ${testsSkipped}"
        exit 1
    fi

    echo "JUnit report OK!"
}
