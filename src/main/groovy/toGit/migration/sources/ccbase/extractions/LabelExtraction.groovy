package toGit.migration.sources.ccbase.extractions
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot

class LabelExtraction extends Extraction {

    String labelKey

    LabelExtraction(String labelKey) {
        this.labelKey = labelKey;
    }

    @Override HashMap<String, Object> extract(Snapshot snapshot) {
        def l = "cleartool getlabel".execute().text
        return [labelKey: l]
    }
}
