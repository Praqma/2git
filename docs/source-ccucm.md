---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## How it works

To migrate a defined component from a defined stream, a temporary child stream and view are made.
The child stream is rebased to the selected Baselines and the view is updated.
For every baseline, actions can then be done to migrate the view contents to Git in whatever way you see fit.
The view and stream are cleaned up afterwards.

## Defining the source

```
source('ccucm') {
    /* Configure the source here */
}
```

### Configuring the source

#### Required

##### component

Defines the component to migrate.
```
source('ccucm') {
    component 'component:foo@\\myVob'
}
```

##### stream

Defines the stream to migrate from.

```
source('ccucm') {
    stream 'stream:foo_Int@\\myVob'
}
```
#### Recommended

##### workspace

Defines the migration workspace. This will be a temporary view used solely for the migration.
Defaults to a new temp dir in the current working directory.

```
source('ccucm') {
    workspace 'path/to/migration/workspace'
}
```

#### Optional
                     
##### loadComponents

Which files should be checked out when updating the migration view.
`modifiable` checks out all modifiable files, while `all` also includes read-only files. 

Either `all` or `modifiable`. Defaults to `modifiable`.

Defines the project on which the temporary migration child stream should be made.
Defaults to the target stream.

```
source('ccucm') {
    loadComponents 'all'
}
```

##### migrationProject

Defines the project on which the temporary migration child stream should be made.
Defaults to the target stream.

```
source('ccucm') {
    migrationProject 'Migration'
}
```

##### readOnlyMigrationStream

Controls if the temporary migration stream should be read-only or not.
May have a positive effect on performance.
Defaults to `false`.

```
source('ccucm') {
    readOnlyMigrationStream true
}
```
**Note:** May break when combined with `loadComponents 'modifiable'`

## CCUCM-specific criteria

### Baseline creation date

#### afterBaseline

`afterBaseline (String baselineName)`

```groovy
criteria {
    afterBaseline 'v1.0.0\\@myVob'
}
```

#### afterDate

`afterDate (Date date)`

```groovy
criteria {
    afterDate (new Date() - 100) // 100 days ago
}
```

`afterDate (String format, String date)`

```groovy
criteria {
    afterDate 'dd-MM-yyyy', '20-06-2010'
}
```

### Baseline name

#### baselineName

`baselineName (String regex)`

```groovy
criteria {
    baselineName(/v(\d+\.?){3}/) //ex.: v1.0.12
}
```

#### baselineNames

`baselineNames (String... names)`

```groovy
criteria {
    baselineNames 'v1.0.0\\@myVob', 'v1.2.5\\@myVob', 'v1.5.0\\@myVob'
}
```

`baselineNames (List<String> names)`

```groovy
criteria {
    baselineNames(['v1.0.0\\@myVob', 'v1.2.5\\@myVob', 'v1.5.0\\@myVob'])
}
```

### Promotion level

#### promotionLevels

`promotionLevels (String... promotionLevels)`

```groovy
criteria {
    promotionLevels 'TESTED', 'RELEASED'
}
```

## CCUCM-specific extractions

### Baseline properties

#### baselineProperty

`baselineProperty (Map<String, String> mappingValues)`

```groovy
extractions {
    /* Map the 'shortname' property to the 'name' variable for use in the actions. */
    baselineProperty([name: 'shortname', level: 'promotionLevel'])
}
```
*Note: Property context of a [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java).*
