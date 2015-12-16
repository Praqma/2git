# ClearCase to Git
A Groovy DSL to facilitate migration from ClearCase to Git. The project currently focuses on the migration of ClearCase UCM components to Git repositories. 

## Concept and workflow
The cc-to-git DSL allows you to easily select sets of baselines from a component stream and assign actions for them. 
These actions can be anything but will most likely consist of Git commits, tags, etc.

A temporary child stream is created for the migration, its view acts as the git work tree in the migration.
Every selected baseline is rebased onto the child stream, the view is updated and the actions are executed.
#### Mapping table
| CCUCM         |   | Git        	|
|:-------------:|:-:|:-------------:|
| Component 	|-> | Repository 	|
| Stream    	|-> | Branch     	|
| Baseline  	|-> | Commit      	|

## A quick example
The following example shows off what the migration of a demo component looks like.
#### DSL script
```groovy
migrate {
    vob('\\v_foo') {    // a VOB to migrate from
    	component('c_model') {  // a component we will be migrating
            migrationOptions {      // some migration options we can set
                git {
                    dir 'e:\\cc-to-git\\model\\.git'        // the git repository path
                    workTree 'e:\\cc-to-git\\model\\view'   // the git work tree path
                    ignore '*.log', 'junk.txt'              // files to add to .gitignore
                    user 'praqma'                           // the Git user name
                    email 'support@praqma.net'              // the Git user mail
                }
            }
            stream('s_model_int') { // the stream we will be migrating
                branch 'master'         // optionally set the target branch name, defaults to stream name
                migrationSteps {
                	// baselines are selected and acted upon in steps through filters
                    filter {
                    	// criteria for selecting baselines
                        criteria {
                            afterDate (new Date() - 100) // from the past 100 days  
                            baselineName 'v\\d{1}\\.\\d{1}\\.\\d{1}' // matching this regex ex.: v1.2.0
                        }
                        // map values for use in actions
                        extractions {
                            baselineExtractor([myName: 'shortname']) // evaluates&maps the baseline shortname
                        }
                        // register actions to run for these baselines
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
* `7ffbaa4` v1.1.0 (HEAD -> master, tag: RELEASED-v1.1.0) 
* `bb84137` v1.0.1
* `13d4ac4` v1.0.0 (tag: RELEASED-v1.0.0)

## Running the DSL
Write your DSL code in the `command.groovy` file and run `ClearCaseToGit.groovy`.

OR

Run `ClearCaseToGit.groovy` after setting the DSL as the `CCTOGIT_COMMAND` environment variable.

## Filter features
### Criteria
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
### Extractions
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
### Actions
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

## More examples
Cookie cutter migration script.
```groovy
def vobName = '\\2Cool_PVOB'
def componentName = 'Client' 
def streamName = 'Client_int'  
def gitDir = "E:\\cc-to-git\\$componentName\\.git"
def gitWorkTree = "E:\\cc-to-git\\$componentName\\view"
def startDate = '31-05-2012'

migrate{
    vob(vobName) {
        component(componentName) {
            migrationOptions {
                git {
					dir	gitDir
					workTree gitWorkTree
                    ignore 'build.log', 'test.log'
                    user 'praqma'
                    email 'support@praqma.net'
                }
            }
            stream(streamName) {
				branch 'master'
                migrationSteps {
                    filter {
                        criteria {
							afterDate 'dd-MM-yyy', startDate
							promotionLevels 'TESTED', 'RELEASED'
                        }
                        extractions {
                            baselineProperty([name: 'shortname', fqname: 'fqname'])
                        }
                        actions {
							git 'add .'
							git 'commit -m"$name"'
							git 'notes add -m"$fqname" HEAD' 
                        }
                    }
					filter {
						criteria {
							promotionLevels 'RELEASED'
						}
						extractions {
							baselineProperty([level: 'promotionLevel'])
						}
						actions {
							git 'tag \"$level-$name\"'  // tag released baselines
						}
					}
                }
            }
        }
    }
}
```
In the above example we add the fully qualified baseline name as a note to the commit.
You can use this to have the migration continue from where it last left off.
```groovy
criteria {
    def lastBaselineName = "git --git-dir $gitWorkTree notes show HEAD".execute().text
	afterBaseline lastBaselineName
    promotionLevels 'TESTED', 'RELEASED'
}
```
