package io.github.rabinarayanpatra.enumx;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;
import io.github.rabinarayanpatra.enumx.annotations.Hide;
import io.github.rabinarayanpatra.enumx.core.EnumMetadata;
import io.github.rabinarayanpatra.enumx.core.EnumRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumRegistryCoverageTest {

    private final EnumRegistry registry = new EnumRegistry();

    @Test
    @DisplayName("register captures exposed and filterable metadata")
    void registerCollectsMetadata() {
        registry.register(MetadataEnum.class);
        EnumMetadata metadata = registry.getByPath("metadata");
        assertThat(metadata).isNotNull();
        assertThat(metadata.getExposedFields()).hasSize(3);
        assertThat(metadata.getFilterableFields()).containsKeys("code", "region");
        assertThat(registry.getByClass(MetadataEnum.class)).isEqualTo(metadata);
        assertThat(registry.getAllMetadata()).contains(metadata);

        metadata.getExposedFields().forEach(fieldMetadata -> {
            assertThat(fieldMetadata.getFieldName()).isNotBlank();
            assertThat(fieldMetadata.getApiName()).isNotBlank();
            assertThat(fieldMetadata.getField()).isNotNull();
            assertThat(fieldMetadata.getFieldType()).isNotNull();
            if (fieldMetadata.isFilterable()) {
                assertThat(fieldMetadata.getFilterName()).isNotBlank();
            }
        });
    }

    @Test
    @DisplayName("duplicate API names are rejected")
    void duplicateApiNamesRaiseError() {
        assertThatThrownBy(() -> registry.register(DuplicateApiName.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate API field name");
    }

    @Test
    @DisplayName("duplicate filter names are rejected")
    void duplicateFilterNamesRaiseError() {
        assertThatThrownBy(() -> registry.register(DuplicateFilterName.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate filter name");
    }

    @Test
    @DisplayName("missing getter when includeAllFields is true triggers error")
    void missingGetterWithIncludeAllFieldsThrows() {
        assertThatThrownBy(() -> registry.register(MissingGetter.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must declare an accessible getter");
    }

    @EnumApi(path = "metadata", includeAllFields = true, keyField = "code")
    enum MetadataEnum {
        ALPHA("A", "Alpha", true, Region.AMERICAS),
        BETA("B", "Beta", false, Region.EUROPE);

        @Filterable
        private final String code;

        @Expose("displayName")
        private final String label;

        @Hide
        private final boolean internal;

        @Filterable
        private final Region region;

        MetadataEnum(String code, String label, boolean internal, Region region) {
            this.code = code;
            this.label = label;
            this.internal = internal;
            this.region = region;
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        public boolean isInternal() {
            return internal;
        }

        public Region getRegion() {
            return region;
        }

        enum Region {
            AMERICAS, EUROPE
        }
    }

    @EnumApi(path = "duplicate-api", includeAllFields = true)
    enum DuplicateApiName {
        SAMPLE;

        @Expose("shared")
        private final String first = "one";

        @Expose("shared")
        private final String second = "two";

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }
    }

    @EnumApi(path = "duplicate-filter", includeAllFields = true)
    enum DuplicateFilterName {
        SAMPLE;

        @Filterable("dup")
        private final String first = "one";

        @Filterable("dup")
        private final String second = "two";

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }
    }

    @EnumApi(path = "missing-getter", includeAllFields = true)
    enum MissingGetter {
        SAMPLE;

        private final String label = "value";

        // No getter on purpose to trigger validation
    }

    @Test
    @DisplayName("filterable field is included even when includeAllFields is false")
    void filterableFieldIncludedWhenIncludeAllFalse() {
        registry.register(FilterOnly.class);
        EnumMetadata metadata = registry.getByPath("filters-only");
        assertThat(metadata.getExposedFields()).hasSize(1);
        assertThat(metadata.getFilterableFields()).containsKey("rank");
    }

    @EnumApi(path = "filters-only", includeAllFields = false)
    enum FilterOnly {
        SAMPLE;

        @Filterable
        private final int rank = 5;

        private final String description = "ignored";

        public int getRank() {
            return rank;
        }
        // No getter for description to ensure it is skipped
    }
}
