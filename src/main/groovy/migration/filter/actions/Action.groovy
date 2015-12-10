package migration.filter.actions

abstract class Action {
    def abstract void act(HashMap<String, Object> extractionMap)
}
