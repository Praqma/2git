---
layout:            2git
organization:      Praqma
repo:              2git
title:             SVN support
---

# Subversion support

Due to the popularity of both VCSs, Subversion to git migrations are plentiful. Though few offer the flexibility that a 2git migration could. 
We'd like to add support for SVN, allowing users to enjoy a highly customizable migration from Subversion to git. 

## Solution

As usual, supporting a new VCS to migrate from includes writing a MigrationSource and exposing it to the DSL. 
For usability's sake, we should include commonly used criteria and extractions.

Functional tests can rely on one of many SVN Docker images.

```
source('subversion') {
    server 'http://bitbucket.redengineering.com:7990/'
    credentials env.SVN_CREDS
}

//...
    criteria {
        branch 'master'
    }
```

## Timebox

Given a week, a minimum viable product containing a configurable source and a handful of criteria/extractions is manageable.