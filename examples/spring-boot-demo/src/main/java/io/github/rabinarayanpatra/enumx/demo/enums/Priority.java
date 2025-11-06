package io.github.rabinarayanpatra.enumx.demo.enums;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;

@EnumApi(path = "priorities", includeAllFields = true)
public enum Priority {
    LOW("Low", 1, Category.NON_BLOCKING),
    MEDIUM("Medium", 2, Category.NON_BLOCKING),
    HIGH("High", 3, Category.BLOCKING),
    CRITICAL("Critical", 4, Category.BLOCKING);

    @Expose("displayName")
    private final String label;

    @Filterable("level")
    private final int severity;

    @Filterable
    private final Category category;

    Priority(String label, int severity, Category category) {
        this.label = label;
        this.severity = severity;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public int getSeverity() {
        return severity;
    }

    public Category getCategory() {
        return category;
    }

    public enum Category {
        BLOCKING,
        NON_BLOCKING
    }
}
