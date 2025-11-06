package io.github.rabinarayanpatra.enumx.core;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;
import io.github.rabinarayanpatra.enumx.annotations.Hide;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        Set<String> apiNames = new HashSet<>();
        Set<String> filterNames = new HashSet<>();

        for (Field field : enumClass.getDeclaredFields()) {
            if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (field.isAnnotationPresent(Hide.class)) {
                continue;
            }

            boolean explicitlyIncluded = field.isAnnotationPresent(Expose.class)
                    || field.isAnnotationPresent(Filterable.class);

            Optional<Method> getter = findGetter(field);
            if ((explicitlyIncluded || annotation.includeAllFields()) && getter.isEmpty()) {
                throw new IllegalStateException(String.format(
                        "Field %s.%s must declare an accessible getter to be exposed or filtered",
                        enumClass.getName(), field.getName()));
            }

            if (!explicitlyIncluded && !annotation.includeAllFields()) {
                continue;
            }

            if (getter.isEmpty()) {
                continue;
            }

            EnumFieldMetadata fieldMeta = buildFieldMetadata(field);
            if (!apiNames.add(fieldMeta.getApiName())) {
                throw new IllegalStateException(String.format(
                        "Duplicate API field name '%s' detected for enum %s",
                        fieldMeta.getApiName(), enumClass.getName()));
            }
            exposedFields.add(fieldMeta);

            if (fieldMeta.isFilterable()) {
                if (!filterNames.add(fieldMeta.getFilterName())) {
                    throw new IllegalStateException(String.format(
                            "Duplicate filter name '%s' detected for enum %s",
                            fieldMeta.getFilterName(), enumClass.getName()));
                }
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

    private Optional<Method> findGetter(Field field) {
        Class<?> declaringClass = field.getDeclaringClass();
        String fieldName = field.getName();
        String capitalizedName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        List<String> candidates = new ArrayList<>();
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            if (fieldName.startsWith("is") && fieldName.length() > 2 &&
                    Character.isUpperCase(fieldName.charAt(2))) {
                candidates.add(fieldName);
            }
            candidates.add("is" + capitalizedName);
        }
        candidates.add("get" + capitalizedName);

        for (String candidate : candidates) {
            try {
                return Optional.of(declaringClass.getMethod(candidate));
            } catch (NoSuchMethodException ignored) {
                // Try next candidate
            }
        }
        return Optional.empty();
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
