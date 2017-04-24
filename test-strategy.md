# Test strategy and architecture

When changing code, we have the following goals:

* all new code must be tested in an automated reproducible manner
* changes to existing code must have improved testing, even if there is not existing tests in that area


## Tests types and priorities

We will be doing fully automated and reproducible tests in the project, prioritized in the following order - at least one needs to apply:

* prefer unit testings if possible
* functional testings when possible
  * some real integration tests that needs to interact with live systems for stability
  * using moc data for queries can be okay if not having access to live systems
* build up a regression test suite, possible re-use of above tests, to ensure primary use-cases are always working

## Test suites

All tests are collected into packages or suites, some are worth highlighting:

* smoke test suite is run early in our build and delivery pipeline to ensure basic functionality works and the tool can run
* regression test suite ensures we keep all functinonality working, and is a huge collection of important tests
* use-case tests are a collection of use-cases that serves to puposes: 1) ensures our tools works with supported common use-cases; 2) documents the functionality of our systems - what can it do

### Examples

Examples on the above test types can be found in the code:

* unit test, see FIXME
* integration tests, live systems, see FIXME
* integration tests using moc data, see FIXME
* regresion test suite, see FIXME

## Test architecture

Beside the usual obvious test architecture and good practices like self contained tests, re-useable test code, small building blocks of tests test we re focussing specifically on re-useability.

We want to be able to use building blocks of functional tests, that can be used also to create demonstrations by running one or several tests in certain order to show a result.


# Apporach with Third-parties and proprietary systems

2git is going to support a lot of non-open, non-free systems, that need licensing, and probably large setup to be able to just run some simple tests.

In case we can't do a mock-up of those systems, the approach will be to make available test-suite that stake-holders can run on their proprietary system and report results back to the project.

We will do that but making test suites completely automated, and configureable. We imagine (yes, we have achieved this yet) that when we implement support for a new systems in collaboration with a stake-holder we can temporarily use their system to develop the test-suite against. It could be the case when making 2git support ClearCaseUCM.

The test suite will take a configuration, very specific to that system we use, but the tests will be the same.
Then we imagine that we can have another user, with the same system, run the same test-suite just editing the configuration file so it matches their local system configuration.

To achieve this we need to design for such tests being:

* complete automated, in all phases, especially in the configuration and setup phase were everything need to be made ready for the test to run
* being so generic they will run on any such system setup that is usual
* make the test report results back in a format (anonymous), we can receive and use to trace tests for a specific version and make compatibility report
* require to get access to the proprietary systems during development
* make sure the test setup is easy to run, e.g. using Docker or similar.
