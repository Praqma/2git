# ClearCase to Git
A Groovy DSL to facilitate migration from ClearCase to Git. The project currently focuses on the migration of ClearCase UCM components to Git repositories. 
## Concept
The ClearCase to Git DSL allows users to define steps to migrate their ClearCase components to Git repositories. 

After choosing which Stream to retrieve the Component Baselines from, Baseline subsets can easily be selected and acted upon, selecting which Baselines to migrate as commits and which of those to tag, etc...

### Mapping table

| CCUCM         |   | Git        	|
|:-------------:|:-:|:-------------:|
| Component 	|-> | Repository 	|
| Stream    	|-> | Branch     	|
| Baseline  	|-> | Commit      	|

## Example
The following example just shows off what the migration of a demo component looks like.
#### DSL script
```java
// Set the output path for the Git repositories.
migrate('/home/natalie/sandbox') {
	// Pick a Vob to migrate from
    vob('\\v_foo') {
    	// Pick a Component to migrate
        component('c_model') {
        	// Pick a Stream from which the Baselines will be migrated
            stream('s_model_int') {
                migrationSteps {
                	// A migration step is represented by a filter
                    filter {
                    	// Filter Baselines using given criteria
                        criteria {
                            baselineName /v\d{1}\.\d{1}\.\d{1}/ //ex.: v1.2.0
                        }
                        // Extract values to a map, which can be used in the actions
                        extractions {
                            baselineExtractor([name: 'shortname'])
                        }
                        // Execute given actions for these baselines
                        actions {
                            git 'add -A'
                            git 'commit -m\"$name\"'
                        }
                    }
                    // FURTHER filter Baselines using given criteria
                    filter {
                        criteria {
                            promotionLevels 'RELEASED'
                        }
                        extractions {
                            baselineExtractor([level: 'promotionLevel'])
                        }
                        actions {
                            git 'tag \"$level-$name\"'
                        }
                    }
                }
            }
        }
    }
}
```
#### Resulting repository
* `7ffbaa4` v1.1.0 (HEAD -> s_model_int, tag: RELEASED-v1.1.0) 
* `bb84137` v1.0.1
* `13d4ac4` v1.0.0 (tag: RELEASED-v1.0.0)

## Running the DSL
Tweak the `command.groovy`, then run `ClearCaseToGit.groovy`.

## Features
### Filter criteria
##### Baseline creation date
`afterBaseline (String baselineName)`
```groovy
criteria {
    afterBaseline 'v1.0.0\\@myVob'
}
```
`afterDate (Date date)`
```groovy
criteria {
    afterDate (new Date() - 100) // 100 days ago
}
```
`afterDate (String format, String date)`
```groovy
criteria {
    afterDate 'dd-MM-yyy', '20-06-2010'
}
```
##### Baseline name
`baselineName (String regex)`
```groovy
criteria {
    baselineName /v\d{1}\.\d{1}\.\d{1}/ //ex.: v1.2.0
}
```
##### Promotion level
`promotionLevels (String... promotionLevels)`
```groovy
criteria {
    promotionLevels 'TESTED', 'RELEASED'
}
```
## Extractions
#### Baseline properties
`baselineProperty (Map<String, String> mappingValues)`
```groovy
extractions {
    /* Map the 'shortname' property to the 'name' variable for use in the Actions.
    *  Use $name to reference the baseline's shortname. */
    baselineProperty [name: 'shortname']
}
```
*Note: See the [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java) and its superclasses for viable properties to map.*
## Actions
#### CLI commands
`cmd(String command)`
```groovy
actions {
    cmd 'echo Today is a good day.'
}
```
`cmd(String command, String path)`
```groovy
actions {
    cmd 'echo Finished a baseline. >> output.log', 'D:\\migration\\logging'
}
```
#### Git commands
`git (String command)`
```groovy
actions {
    git 'commit -m\"$name\"''
}
```
