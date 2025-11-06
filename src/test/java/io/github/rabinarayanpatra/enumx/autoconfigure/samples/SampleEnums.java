package io.github.rabinarayanpatra.enumx.autoconfigure.samples;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;

public final class SampleEnums {
    private SampleEnums() {}

    @EnumApi(path = "alpha-values", includeAllFields = true)
    public enum AlphaEnum {
        ONE("First", true),
        TWO("Second", false);

        @Expose("label")
        private final String name;

        @Filterable
        private final boolean active;

        AlphaEnum(String name, boolean active) {
            this.name = name;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }
    }
}
