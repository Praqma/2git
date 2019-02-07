---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## How it works

A view is created for the purpose of the migration.
For each matching label, a provided config spec template is updated, and the view is updated.
Actions can then run to migrate the view contents however you see fit.

## Defining the source

```groovy
source('clearcase') {
    /* Configure the source here */
}
```

### Configuring the source

#### Required

##### configSpec

The path to the config spec template as a String.
Any occurences of `$label` in the template will be replaced with the label currently being handled.

```groovy
source('clearcase') {
    configSpec 'cc2git/data/product_x.cs'
}
```

##### labelVob/labelFile

One of these must be configured:

* `labelVob`: the VOB from which labels be fetched as a String.
* `labelFile`: path to a file from which labels will be read as a String.

```groovy
source('clearcase') {
    labelVob 'product_x'
}
```

```groovy
source('clearcase') {
    labelFile 'cc2git/data/product_x_labels.txt'
}
```

##### vobPaths

The paths to load into the view as a List of String.
These will be added to the config spec template automatically.

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

Checks if the label _alphabetically_ comes after the given label name.

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
