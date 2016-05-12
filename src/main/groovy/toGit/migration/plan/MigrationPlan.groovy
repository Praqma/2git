package toGit.migration.plan

import toGit.migration.MigrationManager

class MigrationPlan {
    List<Filter> filters = []

    List<Action> befores = []
    Map<String, SnapshotPlan> steps = [:]
    List<Action> afters = []

    void fill() {
        steps = PlanBuilder.buildMigrationPlan(filters)
    }

    void execute() {
        def extractionMap = [:]

        //Execute befores
        befores.each { action ->
            action.act(extractionMap)
        }

        //Run plan
        steps.values().each { step ->
            MigrationManager.instance.source.checkout(step.snapshot)
            step.extractions.each { extraction ->
                extraction.extract(step.snapshot).entrySet().each { kv ->
                    extractionMap.put(kv.key, kv.value)
                }
            }

            step.actions.each { action ->
                action.act(extractionMap)
            }
        }

        //Execute afters
        afters.each { action ->
            action.act(extractionMap)
        }
    }
}
