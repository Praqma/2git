package toGit.migration.sources.mercurial.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.mercurial.MercurialChangeset

class MercurialAfterDate extends Criteria {
    Date date

    MercurialAfterDate(String format, String date) {
        this.date = new Date().parse(format, date)
    }

    MercurialAfterDate(Date date) {
        this.date = date
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def commit = ((MercurialChangeset) snapshot)
        println "Testing '$commit.identifier ($commit.date)' against date '$date'."
        def result = commit.date > date
        println "Result MercurialAfterDate: " + (result ? "SUCCESS" : "FAILURE")
        return result
    }
}
