package all2all.migration.plan

abstract class Action {
    def abstract void act(HashMap<String, Object> extractionMap)
}
