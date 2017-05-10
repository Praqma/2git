# Test strategy and architecture

When changing code, we have the following goals:

* all new code is tested in an automatically reproducible manner
* changes to existing code are accompanied with improved tests, even if there were no existing tests for that area


## Tests types and priorities

We will be doing fully automated and reproducible tests in the project, prioritized in the following order - at least one needs to apply:

* Unit tests
* Functional tests
  * integration tests that interact with live/containerized systems
  * functional tests running against mock-ups
* Regression tests, ensuring primary use-cases are always working

## Test suites

All tests are collected into packages or suites, some are worth highlighting:

* Smoke test suite: quick tests that run early in the build, ensuring basic functionality works
* Regression test suite: ensures all current functionality keeps working
* Use-case tests suite: ensures the tools works with common supported use-cases, serves as documentation of tool functionality

### Examples

Examples on the above test types can be found in the code:

* unit tests: see FIXME
* integration tests, live systems: see FIXME
* integration tests, mock systems: see FIXME
* regression tests: see FIXME

# Third-party and proprietary systems

2git will support a lot of closed, proprietary systems that require licensing and bombastic setups just to run simple tests.

In case we can't do a mock-up of these systems, stakeholders can run test suites on their proprietary system and report results back to the project.
When we implement support for a new systems in collaboration with a stakeholder, we can temporarily use their system to develop the suite. 

The test suite will take a configuration file, and likely a small set-up script.
Separate users, with the same proprietary system, can run the same test suite by simply editing the configuration file to match their system configuration.

To achieve this, tests need to:

* be complete automated, especially the configuration and setup phase.
* be generic, so they can run on any system.
* output results back in a format we can use to trace tests to a specific version and make compatibility reports
* have access to related proprietary systems during development
* have a setup that is easy to run, e.g. Docker
