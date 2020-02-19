package collector.hotwheels.insight.imports.model;

public enum ModuleSelector {
    /**
     * Selectors, added key selector for external use so we can change the selector names without breaking customers
     * configurations
     */

    CATEGORY("CATEGORY"),
    MODEL("MODEL"),
    TOY("TOY"),
    COLLECTION("COLLECTION"),

    UNKNOWN("");

    private String selector;

    private ModuleSelector(String selector) {
        this.selector = selector;
    }

    public String getSelector() {
        return selector;
    }

    public synchronized static ModuleSelector getInstance(String operator) {

        ModuleSelector moduleSelector = null;
        for (ModuleSelector s : values()) {
            if (s.getSelector()
                    .equals(operator.toUpperCase())) {
                moduleSelector = s;
                break;
            }
        }

        return moduleSelector != null ? moduleSelector : ModuleSelector.UNKNOWN;
    }
}
