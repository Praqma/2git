package migration.sources.ccucm.context

import context.base.Context
import context.traits.HasExtractions
import migration.sources.ccucm.CcucmExtractions

trait CcucmExtractionsContext implements Context, HasExtractions {

    /**
     * Extracts a CoolBaseline property
     * @param map A map of values to extract and keys to map them to.
     */
    void baselineProperty(Map<String, String> map) {
        extractions.add(new CcucmExtractions.BaselineProperty(map))
    }
}
