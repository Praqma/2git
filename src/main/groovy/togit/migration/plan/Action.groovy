package togit.migration.plan

abstract class Action {
    abstract void act(Map<String, Object> extractionMap)
}
