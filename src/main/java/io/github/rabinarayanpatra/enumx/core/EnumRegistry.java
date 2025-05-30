package io.github.rabinarayanpatra.enumx.core;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;
import io.github.rabinarayanpatra.enumx.annotations.Hide;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that holds metadata for all enums marked with @EnumApi.
 */
@Component
public class EnumRegistry {
    
    private final Map<String, EnumMetadata> pathToMetadata = new ConcurrentHashMap<>();
    private final Map<Class<?>, EnumMetadata> classToMetadata = new ConcurrentHashMap<>();
    
    public void register(Class<? extends Enum<?>> enumClass) {
        EnumApi annotation = enumClass.getAnnotation(EnumApi.class);
        if (annotation == null) {
            return;
        }
        

        EnumMetadata metadata = buildMetadata(enumClass, annotation);
        pathToMetadata.put(annotation.path(), metadata);
        classToMetadata.put(enumClass, metadata);
    }
    
    public EnumMetadata getByPath(String path) {
        return pathToMetadata.get(path);
    }
    
    public EnumMetadata getByClass(Class<?> enumClass) {
        return classToMetadata.get(enumClass);
    }
    
    public Collection<EnumMetadata> getAllMetadata() {
        return pathToMetadata.values();
    }
    
    private EnumMetadata buildMetadata(Class<? extends Enum<?>> enumClass, EnumApi annotation) {
        List<EnumFieldMetadata> exposedFields = new ArrayList<>();
        Map<String, EnumFieldMetadata> filterableFields = new HashMap<>();
        
        // Process all declared fields
        for (Field field : enumClass.getDeclaredFields()) {
            // Skip synthetic fields (like $VALUES)
            if (field.isSynthetic()) {
                continue;
            }
            
            // Skip static fields
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            // Check if field should be included
            boolean shouldInclude = shouldIncludeField(field, annotation.includeAllFields());
            if (!shouldInclude) {
                continue;
            }
            
            // Build field metadata
            EnumFieldMetadata fieldMeta = buildFieldMetadata(field);
            exposedFields.add(fieldMeta);
            
            // Check if filterable
            if (fieldMeta.isFilterable()) {
                filterableFields.put(fieldMeta.getFilterName(), fieldMeta);
            }
        }
        
        return EnumMetadata.builder()
                .enumClass(enumClass)
                .path(annotation.path())
                .keyField(annotation.keyField())
                .includeAllFields(annotation.includeAllFields())
                .exposedFields(exposedFields)
                .filterableFields(filterableFields)
                .build();
    }
    
    private boolean shouldIncludeField(Field field, boolean includeAllFields) {
        // @Hide always wins
        if (field.isAnnotationPresent(Hide.class)) {
            return false;
        }
        
        // @Expose always includes
        if (field.isAnnotationPresent(Expose.class)) {
            return true;
        }
        
        // Otherwise, check includeAllFields and if field has getter
        return includeAllFields && hasGetter(field);
    }
    
    private boolean hasGetter(Field field) {
        String fieldName = field.getName();
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String booleanGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        
        try {
            field.getDeclaringClass().getMethod(getterName);
            return true;
        } catch (NoSuchMethodException e1) {
            try {
                // For boolean fields, check isXxx() method
                if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    field.getDeclaringClass().getMethod(booleanGetterName);
                    return true;
                }
            } catch (NoSuchMethodException e2) {
                // No getter found
            }
        }
        return false;
    }
    
    private EnumFieldMetadata buildFieldMetadata(Field field) {
        field.setAccessible(true);
        
        // Get API name
        String apiName = field.getName();
        Expose expose = field.getAnnotation(Expose.class);
        if (expose != null && !expose.value().isEmpty()) {
            apiName = expose.value();
        }
        
        // Check if filterable
        boolean filterable = field.isAnnotationPresent(Filterable.class);
        String filterName = apiName;
        if (filterable) {
            Filterable filterableAnn = field.getAnnotation(Filterable.class);
            if (!filterableAnn.value().isEmpty()) {
                filterName = filterableAnn.value();
            }
        }
        
        return EnumFieldMetadata.builder()
                .fieldName(field.getName())
                .apiName(apiName)
                .field(field)
                .filterable(filterable)
                .filterName(filterName)
                .fieldType(field.getType())
                .build();
    }
}