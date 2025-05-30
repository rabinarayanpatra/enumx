package io.github.rabinarayanpatra.enumx.core;

import java.util.List;
import java.util.Map;

/**
 * Complete metadata for an enum exposed via API.
 */
public class EnumMetadata {
    private Class<? extends Enum<?>> enumClass;
    private String path;
    private String keyField;
    private boolean includeAllFields;
    private List<EnumFieldMetadata> exposedFields;
    private Map<String, EnumFieldMetadata> filterableFields;

    // Constructor
    public EnumMetadata(Class<? extends Enum<?>> enumClass, String path, String keyField,
                        boolean includeAllFields, List<EnumFieldMetadata> exposedFields,
                        Map<String, EnumFieldMetadata> filterableFields) {
        this.enumClass = enumClass;
        this.path = path;
        this.keyField = keyField;
        this.includeAllFields = includeAllFields;
        this.exposedFields = exposedFields;
        this.filterableFields = filterableFields;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Class<? extends Enum<?>> enumClass;
        private String path;
        private String keyField;
        private boolean includeAllFields;
        private List<EnumFieldMetadata> exposedFields;
        private Map<String, EnumFieldMetadata> filterableFields;

        public Builder enumClass(Class<? extends Enum<?>> enumClass) {
            this.enumClass = enumClass;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder keyField(String keyField) {
            this.keyField = keyField;
            return this;
        }

        public Builder includeAllFields(boolean includeAllFields) {
            this.includeAllFields = includeAllFields;
            return this;
        }

        public Builder exposedFields(List<EnumFieldMetadata> exposedFields) {
            this.exposedFields = exposedFields;
            return this;
        }

        public Builder filterableFields(Map<String, EnumFieldMetadata> filterableFields) {
            this.filterableFields = filterableFields;
            return this;
        }

        public EnumMetadata build() {
            return new EnumMetadata(enumClass, path, keyField, includeAllFields, exposedFields, filterableFields);
        }
    }

    // Getters
    public Class<? extends Enum<?>> getEnumClass() { return enumClass; }
    public String getPath() { return path; }
    public String getKeyField() { return keyField; }
    public boolean isIncludeAllFields() { return includeAllFields; }
    public List<EnumFieldMetadata> getExposedFields() { return exposedFields; }
    public Map<String, EnumFieldMetadata> getFilterableFields() { return filterableFields; }
}
