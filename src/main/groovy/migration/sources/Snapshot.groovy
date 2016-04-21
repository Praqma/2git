package migration.sources

import migration.plan.Criteria

abstract class Snapshot {
    String identifier

    public Snapshot(String identifier) {
        this.identifier = identifier
    }

    boolean matches(ArrayList<Criteria> criteria) {
        for (def crit : criteria) {
            if (!crit.appliesTo(this))
                return false
        }
        return true
    }
}
