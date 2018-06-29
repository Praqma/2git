---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## Defining the target

Define a target with the `'artifactory'` type, optionally followed by your target identifier (in this case, `'art'`).

```groovy
target('artifactory', 'art') {
    /* Configure the target here */
}
```

### Configuring the target

#### Required

##### url

Defines the Artifactory url

```groovy
target('artifactory') {
    host 'artifactory.blumanufacturing.com:8081/artifactory'
}
```

##### repository

Defines the Artifactory repository

```groovy
target('artifactory') {
    repository 'libs-snapshot-local'
}
```

#### Optional

##### user/password

Defines the Artifactory credentials

```groovy
target('artifactory') {
    user 'art-user'
    password 'mYSecrEt2000'
}
```

### Actions

#### Publish

Publishes a file to Artifactory

```groovy
actions {
    def artifact = new File('build/client.zip')
    art.publish("com/blu/foober/${version}/client-${version}.zip", artifact)
}
```
