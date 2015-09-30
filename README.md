### Not ready for migration of production environments.
# ClearCase to Git
A Groovy DSL to facilitate migration from ClearCase to Git. The project currently focuses on the migration of ClearCase UCM components to Git repositories. 
## Concept
The ClearCase to Git DSL allows users to define steps to migrate their ClearCase components to Git repositories. 

After choosing which Stream to retrieve the Component Baselines from, Baseline subsets can easily be selected and acted upon, slecting which Baselines to migrate as commits and which of those to tag, etc...

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
                            baselineName '/v\d{1}\.\d{1}\.\d{1}/' //ex.: v1.2.0
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
Tweak the `command.groovy`, then run `ClearCaseToGit.groovy`