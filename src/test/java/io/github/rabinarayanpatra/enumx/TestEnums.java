package io.github.rabinarayanpatra.enumx;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;
import io.github.rabinarayanpatra.enumx.annotations.Hide;

/**
 * Test enums for unit tests.
 */
public class TestEnums {

    @EnumApi(path = "simple-status")
    public enum SimpleStatus {
        ACTIVE, INACTIVE
    }

    @EnumApi(path = "roles", includeAllFields = true)
    public enum Role {
        ADMIN("Administrator", true, "ADM"),
        USER("Regular User", false, "USR"),
        GUEST("Guest User", false, "GST");

        @Expose("displayName")
        private final String label;

        @Filterable("active")
        private final boolean isActive;

        @Hide
        private final String code;

        Role(String label, boolean isActive, String code) {
            this.label = label;
            this.isActive = isActive;
            this.code = code;
        }

        public String getLabel() {
            return label;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getCode() {
            return code;
        }
    }

    @EnumApi(path = "countries", keyField = "code")
    public enum Country {
        UNITED_STATES("US", "United States"),
        CANADA("CA", "Canada"),
        UNITED_KINGDOM("GB", "United Kingdom");

        @Expose
        private final String code;

        @Expose
        @Filterable
        private final String name;

        Country(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}