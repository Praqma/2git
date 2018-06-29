---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## Script arguments

Both positional or named arguments can be passed into the script.
Positional arguments are accessible through the 'args' variable while the named arguments will be available as regular variables.

`java -jar 2git.jar myDslScript.groovy myArgument`

```groovy
println args[1] // myArgument
```

`java -jar 2git.jar myDslScript.groovy foo="bar and baz"`

```groovy
println foo     // bar and baz
```
