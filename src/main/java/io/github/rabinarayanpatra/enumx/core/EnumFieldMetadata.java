package io.github.rabinarayanpatra.enumx.core;


import java.lang.reflect.Field;

public class EnumFieldMetadata {
    private String fieldName;
    private String apiName;
    private Field field;
    private boolean filterable;
    private String filterName;
    private Class<?> fieldType;

    // Constructor
    public EnumFieldMetadata(String fieldName, String apiName, Field field,
                             boolean filterable, String filterName, Class<?> fieldType) {
        this.fieldName = fieldName;
        this.apiName = apiName;
        this.field = field;
        this.filterable = filterable;
        this.filterName = filterName;
        this.fieldType = fieldType;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fieldName;
        private String apiName;
        private Field field;
        private boolean filterable;
        private String filterName;
        private Class<?> fieldType;

        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder field(Field field) {
            this.field = field;
            return this;
        }

        public Builder filterable(boolean filterable) {
            this.filterable = filterable;
            return this;
        }

        public Builder filterName(String filterName) {
            this.filterName = filterName;
            return this;
        }

        public Builder fieldType(Class<?> fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public EnumFieldMetadata build() {
            return new EnumFieldMetadata(fieldName, apiName, field, filterable, filterName, fieldType);
        }
    }

    // Getters
    public String getFieldName() { return fieldName; }
    public String getApiName() { return apiName; }
    public Field getField() { return field; }
    public boolean isFilterable() { return filterable; }
    public String getFilterName() { return filterName; }
    public Class<?> getFieldType() { return fieldType; }
}