package toGit.migration.plan

abstract class Snapshot {
    String identifier

    public Snapshot(String identifier) {
        this.identifier = identifier
    }

    /**
     * Checks whether this Snapshot matches all given criteria
     * @param criteria A List of Criteria to match the Snapshot against
     * @return true if the Snapshot matches all Criteria, otherwise false
     */
    boolean matches(ArrayList<Criteria> criteria) {
        for (def crit : criteria) {
            if (!crit.appliesTo(this))
                return false
        }
        return true
    }
}
