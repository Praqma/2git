---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

**_Note:_** The ClearCase plugin is a WIP

## How it works

A view is created for the purpose of the migration.
For each given label, its config spec is updated with the given VOBs.

## Defining the source

```groovy
source('clearcase') {
    /* Configure the source here */
}
```

### Configuring the source

#### Required

##### configSpec

The path to the config spec file used in the migration as a String

```groovy
source('clearcase') {
    configSpec '/home/rob/tmp/migration.cs'
}
```

##### labelVob

The VOB from which labels to migrate will be fetched as a String

```groovy
source('clearcase') {
    labelVob 'product_x'
}
```

##### vobPaths

The VOBs to load in the migration view as a List of Strings

```groovy
source('clearcase') {
    vobPaths ['product_x', 'product_y', 'product_z']
}
```

#### Recommended

Defines the migration workspace. This will be a temporary view used solely for the migration.
Defaults to a new temporary directory in the current working directory.

```groovy
source('clearcase') {
    workspace 'path/to/migration/workspace'
}
```

##### viewTag

The tag the migration view will be created with as a String

```groovy
source('clearcase') {
    viewTag 'cc2git-migration'
}
```
[//]: # ()
[//]: # (#### Optional)
[//]: # ()
[//]: # (##### n/a)
[//]: # ()
[//]: # (n/a)
[//]: # ()
[//]: # (```)
[//]: # (source('clearcase'\) {)
[//]: # (n/a 'foo')
[//]: # (})
[//]: # (```)

## Specific criteria

### Label name

#### labelName

Checks if the label matches the given RegEx.

```groovy
criteria {
    labelName("(\\d{6})-(.+)")
}
```

#### afterLabel

Checks if the label comes _alphabetically_ after the given label name.

```groovy
criteria {
    afterLabel("010116-v2.1.3")
}
```

## Specific extractions

### Label

Makes the label available in the action context through the given String key.

```groovy
criteria {
    label('my_label')
}
```
