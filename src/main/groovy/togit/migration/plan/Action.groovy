package togit.migration.plan

abstract class Action {
    abstract void act(HashMap<String, Object> extractionMap)
}
