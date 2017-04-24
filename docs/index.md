---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

The 2git project is an SCM migration engine that enables you to migrate to git using a Groovy DSL.
{: .cuff}

## Introduction

2git is a Groovy DSL, a small language that is designed to describe what you want to migrate - from a source VCS to git.

You write your migration recipe and feed it into the 2git engine, which will then execute your migration, resulting in your git repository.

If you don't like what you see, you can easily tweak your recipe and run it again, until you get the perfect migration.

## General documentation

  * [Workflow and concepts](workflow-and-concepts)
  * [Contact and contributing](contact-and-contributing)

## Technical documentation

  * [Getting started](getting-started)
  * [Source: ClearCase UCM](source-ccucm)
  * [Target: Git](target-git)
  * [Target: artifactory](target-artifactory)
  * [Other features](other-features)

## Roadmap

 * [Vblog](roadmap/vblog)
 * [GitHub support](roadmap/github-support)
 * [Bitbucket support](roadmap/bitbucket-support)
 * [Base ClearCase features](roadmap/baseClearCaseFeatures)
 * [Zip files as source](roadmap/zip-file-source)
