---
  layout:            2git
  organization:      Praqma
  repo:              2git
  title:             GitHub support
---
# Github support

## Problem

When I made my conversion with 2git, I need to make the end-result available.  As conversion is some times a trial and error, there are intermidiary result we might want to look at and let the developers investigate and thus it also have to be made available. These are intermidiary results.

We want to be able to push the resulting version to Github, but as we do this many times we need to be overwriting or making several version of such a resulting conversion available.

## Solution

As part of the conversion configruation, we define a target. The target is an organisation and repository name.
When conversion is done, the result is pushed to org/repo on Github.

The push configuration needs to be configureable to allow for credentials to push as.
To support intermediary results we will allow for adding sequence numbers if the conversion is a trial, while the final conversion will be without sequence numbers.

Example configuration:

    # User for pushing to Github.
    user: myuser
    # Credential type for the user
    # - ssh means ssh key-pair availble
    credential: ssh

    # Target repository org/reponame
    target: praqma/2git-example

    # conversion type, is it a trial or final?
    # Either write 'final' or 'trail'
    # use ENV[name] to get if from environment variable named 'type'
    conversiontype: ENV[type]

    # sequence number scheme
    numberscheme: integer

## Implementation

Scope

* ssh will be the only support for crendtials to start with
* both conversion types will be supported
* automatically incrementing integers will be support as sequence numbers

Technicalities:

* YAML file for configuration
* Github support in a separate module, all common parts isolation so it will only be a few lines to support Bitbucket support
* Testing will be done using mockup of Github API

# Timebox

Around one week of work.
