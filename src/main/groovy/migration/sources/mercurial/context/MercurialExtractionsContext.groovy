package migration.sources.mercurial.context

import context.base.Context
import migration.sources.mercurial.extractions.ChangeSetProperties

trait MercurialExtractionsContext implements Context {

    /**
     * Extracts a CoolBaseline property
     * @param map A map of values to extract and keys to map them to.
     */
    void ChangeSetId(Map<String, String> map) {
        extractions.add(new ChangeSetProperties())
    }

}
