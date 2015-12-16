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
```groovy
migrate {
    vob('\\v_foo') {
    	component('c_model') {
            migrationOptions {                              // optional migration settings
                git {
                    dir 'e:\\cc-to-git\\model\\.git'        // target git repository
                    workTree 'e:\\cc-to-git\\model\\view'   // target git work tree
                    ignore '*.log', 'junk.txt'              // add files to the .gitignore file
                    user 'praqma'                           // set the Git user name
                    email 'support@praqma.net'              // set the Git user mail
                }
            }
            stream('s_model_int') {
                branch 'master'     // target Git branch name

                migrationSteps {
                	// baselines are selected and acted upon in steps using filters
                    filter {
                    	// select Baselines using criteria
                        criteria {
                            baselineName 'v\\d{1}\\.\\d{1}\\.\\d{1}' //ex.: v1.2.0
                        }
                        // extract values to a map for use in actions
                        extractions {
                            baselineExtractor([myName: 'shortname'])
                        }
                        // execute actions for these baselines
                        actions {
                            git 'add -A'
                            git 'commit -m\"myName\"'
                        }
                    }
                    // further filter previously selected baselines
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
##### Custom
`custom (Closure<Boolean> closure)`
```groovy
criteria {
    custom { baseline ->
        println "Testing baseline name length limit."
		return baseline.shortname.length <= 10
    }
}
```
*Note: The parameter passed in is a [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java).*
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
*Note: Runs in the context of a [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java).*
#### Custom
`custom (Closure<HashMap<String, Object>> closure)`
```groovy
extractions {
    /* Return a HashMap you build yourself. */
    custom { baseline ->
        def map = new HashMap<String, Object>()
		map.put('baselineName', baseline.shortname)
		map.put('lostFoundContents', new File('lost+found').text)
		return map
    }
}
```
*Note: The parameter passed in is a [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java).*
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
#### Custom
`custom(Closure closure)`
```groovy
actions {
    custom { map ->
	    println "Executing custom logging action."
	    def timestamp = new Date().toTimestamp()
	    new File("timestamps.log").append("$map.myExtractedValue seen at $timestamp.\n")
	}
}
```
*Note: The parameter passed in is the extraction HashMap<String, Object> created in the Extraction phase.*

#### Git commands
`git (String command)`
```groovy
actions {
    git 'commit -m\"$name\"''
}
```
