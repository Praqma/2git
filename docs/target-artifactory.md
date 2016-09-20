---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## Defining the target

Define a target with the `'artifactory'` type, followed by your target identifier (in this case, `'art'`).

```
target('artifactory', 'art') {
    /* Configure the target here */
}
```

### Configuring the target

#### Required

##### host

Defines the Artifactory host url

```
target('artifactory') {
    host 'artifactory.blumanufacturing.com'
}
```

##### port

Defines the Artifactory host port

```
target('artifactory') {
    host 8081
}
```

#### Optional

##### user/password

Defines the Artifactory credentials

```
target('artifactory') {
    user 'art-user'
    password 'mYSecrEt2000'
}
```

### Actions

#### Publish

Publishes a file to Artifactory

```
actions {
    def artifact = new File('build/client.zip')
    art.publish("/artifactory/libs-snapshot-local/com/blu/foober/${version}/client-${version}.zip", artifact)
}
```