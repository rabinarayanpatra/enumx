package io.github.rabinarayanpatra.enumx.autoconfigure;

import io.github.rabinarayanpatra.enumx.core.EnumFieldMetadata;
import io.github.rabinarayanpatra.enumx.core.EnumMetadata;
import io.github.rabinarayanpatra.enumx.core.EnumRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exposes diagnostic metadata about enums registered with EnumX.
 */
@RestController
@RequestMapping("/enumx/metadata")
class EnumxMetadataController {

    private final EnumRegistry registry;

    EnumxMetadataController(EnumRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    List<EnumMetadataView> getAll() {
        return registry.getAllMetadata().stream()
                .sorted(Comparator.comparing(EnumMetadata::getPath))
                .map(EnumMetadataView::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{path}")
    ResponseEntity<EnumMetadataView> getByPath(@PathVariable String path) {
        EnumMetadata metadata = registry.getByPath(path);
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EnumMetadataView.from(metadata));
    }

    record EnumMetadataView(String enumClass,
                             String path,
                             String keyField,
                             boolean includeAllFields,
                             List<EnumFieldView> fields) {

        static EnumMetadataView from(EnumMetadata metadata) {
            List<EnumFieldView> fields = metadata.getExposedFields().stream()
                    .map(EnumFieldView::from)
                    .collect(Collectors.toList());

            return new EnumMetadataView(
                    metadata.getEnumClass().getName(),
                    metadata.getPath(),
                    metadata.getKeyField(),
                    metadata.isIncludeAllFields(),
                    fields
            );
        }
    }

    record EnumFieldView(String fieldName,
                          String apiName,
                          boolean filterable,
                          String filterName,
                          String type) {

        static EnumFieldView from(EnumFieldMetadata metadata) {
            return new EnumFieldView(
                    metadata.getFieldName(),
                    metadata.getApiName(),
                    metadata.isFilterable(),
                    metadata.getFilterName(),
                    metadata.getFieldType().getName()
            );
        }
    }
}
