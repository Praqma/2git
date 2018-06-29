---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## Defining the target

Define a target with the `'git'` type, optionally followed by your target identifier (in this case, `'g'`).

```groovy
target('git', 'g') {
    /* Configure the target here */
}
```

### Configuring the target

#### Recommended

##### workspace

Defines the migration workspace. This should be the path where the resulting git directory ends up in.
Defaults to a new dir in the current working directory.

```groovy
target('git') {
    workspace 'path/to/git/directory'
}
```

#### Optional

##### ignore

Adds entries to .gitignore.

```groovy
target('git') {
    ignore '*.tmp', 'foo.txt'
}
```

##### user/email

Configures the default repository user/email.
Defaults to a placeholder migration user.

```groovy
target('git') {
    user 'William Spearshake'
    email 'wil@sshake.org'
}
```

##### lfs

Tracks entries with [Git LFS](https://git-lfs.github.com/).

```groovy
target('git') {
    lfs '*.pdf', '*.iso'
}
```

##### longPaths

Sets `core.longpaths` before the migration.

```groovy
target('git') {
    longPaths true
}
```
