# cc2git
A Groovy DSL to facilitate migration from ClearCase to Git. The project currently focuses on the migration of UCM components to Git repositories. 

## Concept and workflow

The cc2git DSL allows you to easily select sets of baselines from component's streams and assign actions to them. 
These actions can be anything but will most likely consist of Git commits, tags, etc.

Per stream a git repository, a temporary child stream and view of said stream is created.
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
migrate{
    vob('\\2Cool_PVOB') {	// the vob to migrate from
        component('_Client') {	// the component to migrate
            migrationOptions {	// some migration options
                git {
					dir	'e:/cc2git/client/repo'		// git repo path
					workTree 'e:/cc2git/client/view'	// git work tree path
                    ignore '*.log', 'tmp'					// git ignore rules
                    user 'praqma'							// git user name
                    email 'support@praqma.net'				// git user mail
                }
                clearCase {
                    components 'all'                        // components to migrate ('all'/'modifiable')
                }
            }
            stream('Client_migr') {	// the stream to select baselines from
				branch 'master'	// set target branch name
                migrationSteps {
					// baselines are selected and acted upon in steps through filters
					filter {
						// criteria for selecting baselines
						criteria {
							afterDate 'dd-MM-yyy', '01-01-2015'	// since 2015
						}
						// register value mappings for use in actions
						extractions {
							baselineProperty([blName: 'shortname']) 
						}
						// register actions to selected baselines
						actions {
							git 'add -A'
							git 'commit -m$blName'
							cmd 'echo $blName >> e:/cc2git/client/migration.log'
						}
						filter {
							criteria {
								promotionLevels 'TESTED', 'RELEASED'
							}
							extractions {
                                baselineProperty([blLevel: 'promotionLevel'])
							}
                            actions {
                                git 'tag $blLevel-$blName'
                            }
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

Execute `Run.groovy` and supply your cc2git dsl script as its first parameter.

ex.: `groovy Run.groovy myScript.groovy`

### Arguments

Both positional or named arguments can be passed into the script.

`groovy Run.groovy myDslScript.groovy myArgument`
```groovy
println args[1] // myArgument
```

`groovy Run.groovy myDslScript.groovy foo="bar and baz"`
```groovy
println foo     // bar and baz
```

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
    afterDate 'dd-MM-yyyy', '20-06-2010'
}
```
##### Baseline name
`baselineName (String regex)`
```groovy
criteria {
    baselineName(/v(\d+\.?){3}/) //ex.: v1.0.12
}
```
##### Custom
`custom (Closure<Boolean> closure)`
```groovy
criteria {
    custom { baseline ->
        println "Testing baseline name length."
		return baseline.shortname.length() <= 10
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
    /* Map the 'shortname' property to the 'name' variable for use in the actions. */
    baselineProperty [name: 'shortname', level: 'promotionLevel']
}
```
*Note: Runs in the context of a [COOL Baseline](https://github.com/Praqma/cool/blob/master/src/main/java/net/praqma/clearcase/ucm/entities/Baseline.java).*
#### Custom
`custom (Closure<HashMap<String, Object>> closure)`
```groovy
extractions {
    // build a custom HashMap
    custom { baseline ->
        // read some data from a file in the view
        String projectVersion = new File("$workTree/foo/model/version.txt").text
        return [version: projectVersion, name: baseline.shortname]
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
    cmd 'echo Today is a good day. >> output.log', 'd:/migration/logging'
}
```
#### Custom
`custom(Closure closure)`
```groovy
actions {
    custom { map ->
        // custom trace messages
	    def timestamp = new Date().toTimestamp()
	    println "Started migrating $map.baselineName at $timestamp"
	}
}
```
*Note: The parameter passed in is the extraction HashMap<String, Object> created by the extractions.*

#### Git commands
`git (String command)`
```groovy
actions {
    git 'commit -m$name'
}
```

## More examples
Cookie cutter migration script.
```groovy
def vobName = '\\2Cool_PVOB'
def componentName = '_Client' 
def streamName = 'Client_migr'  
def startDate = '31-05-2015'
def gitDir = "e:/cc2git/$componentName/.git"
def gitWorkTree = "e:/cc2git/$componentName/view"

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
                clearCase {
                    components 'all'
                }
            }
            stream(streamName) {
				branch 'master'
                migrationSteps {
                    filter {
                        criteria {
							afterDate 'dd-MM-yyy', startDate
							promotionLevels 'INITIAL'
                        }
                        extractions {
                            baselineProperty([name: 'shortname', fqname: 'fqname'])
                        }
                        actions {
							git 'add .'
							git 'commit -m"$name"'
							git 'notes add -m"$fqname" HEAD' 
                        }                    
    					filter {
    						criteria {
    							promotionLevels 'INITIAL'
    						}
    						extractions {
    							baselineProperty([level: 'promotionLevel'])
						    }
						    actions {
						    	git 'tag $level-$name'
					    	}
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

## Other features

### Output read-only/modifiable component baselines

Call from script root to output read-only/modifiable component baselines to a logfile.
Useful for managing dependencies during your migration.

`logDependencies(String fullyQualifiedStreamName, String logFileName)`
```groovy
logDependencies('stream:myStream@\\myVob', 'e:/cc2git/log/dependencies.txt')
```