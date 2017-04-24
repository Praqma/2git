---
layout:            2git
organization:      Praqma
repo:              2git
title:             Bitbucket support
---

# Bitbucket support

Migrating to and pushing from a local Git repo is already possible in 2git, but certain interactions with Bitbucket still require a lot of custom coding.
There's no built-in support for creating, listing or deleting repositories, for example.

## Solution

Allowing users to configure a Bitbucket repository as a target and exposing various actions such as creating, deleting or setting the default branch of this repo can make life much easier for migrating to Bitbucket.

## Implementation

We can add a Bitbucket target, which comes with its own configuration and custom actions.
Configration can be set to point to a specific Bitbucket server/project/repository, allowing interaction through various actions.
Functional tests can rely on the [Bitbucket Docker image](https://bitbucket.org/atlassian/docker-atlassian-bitbucket-server) for spinning up a test environment on the fly.


```
target('bitbucket-server', 'web') {
    server 'http://bitbucket.redengineering.com:7990/'
    project 'BRAVO'
    repo 'web'
}

//...
    actions {
        web.create()
    }
```

### Bitbucket Server

Bitbucket server exposes a simple REST API that can be used to achieve all of this.
The API doesn't support authentication tokens or similar, so extra focus should be put on security.

### Bitbucket Cloud

Bitbucket Cloud exposes a revamped REST API that exposes far more functionality than Bitbucket server.
The API also supports authentication tokens, so securing the calls should be simple.

## Timebox

Simple interactions for both Server and Cloud should be achievable within a week.
