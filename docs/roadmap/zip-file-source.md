---
layout:            2git
organization:      Praqma
repo:              2git
title:             Support versioned zip files as a source for 2git
---

# Idea - Support versioned zip files as source
2Git should be able to migrate anything that represents a ordered list of snapshots. Zip files versioned with a number or a date/time stamp are an obvious and easy to understand example.

We want to support this for a number of reasons:

* Easy to understand showcase.  This is useful for documentation examples and /get up and running/ demos and guides.
* As a framework for mocking source repos when writing functional tests of the  core system and target features. This would allow simple and maintainable test scenarios without depending on external VCS systems.
* As a stop-gap tool for supporting any VCS systems that are not yet supported or not viable to support whole sale. If we can read a list of zip files as input, then we can convert from anything where a user can generate these from their existing tool.

# Solution
Implement a source “/plugin/“  that can read zip files and extract each zip file as a snapshot representing one /state/ of the repository. 
There has to be some sort of pattern-matching for the zip file names, and a configurable  ordering of these.

This epic will also include using this feature to add simple examples to the documentation, and examples of how it can be used in functional tests.

# Implementation
* Allow configuring a regular expression that matches zip file names and extracts the part used for defining ordering.
* Support standard cases for ordering (numeric, lexicographic, date-formats)
* Support a way of providing your own order function (comparator)
* Support basic zip files and tgz.
* Optionally (probably trivial) to support non-biped folders with the same naming conventions.

# Estimate / Timebox
We estimate this as less than a weeks work.
