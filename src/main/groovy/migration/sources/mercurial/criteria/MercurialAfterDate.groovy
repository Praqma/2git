package migration.sources.mercurial.criteria

import migration.plan.Criteria
import migration.sources.Snapshot
import migration.sources.mercurial.MercurialChangeSet

class MercurialAfterDate extends Criteria {
    Date date
    Runtime rt = Runtime.getRuntime()
    ProcessBuilder builder
    Process pr

    MercurialAfterDate(String format, String date) {
        this.date = new Date().parse(format, date)
    }

    MercurialAfterDate(Date date) {
        this.date = date
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def commit = ((MercurialChangeSet) snapshot)
        //println "Testing '" + commit.identifier + " (" + commit.date + ")' against date '" + date + "'."
        def result = commit.date > date
        println "Result MercurialAfterDate: " + (result ? "SUCCESS" : "FAILURE")
        return result
    }
}
