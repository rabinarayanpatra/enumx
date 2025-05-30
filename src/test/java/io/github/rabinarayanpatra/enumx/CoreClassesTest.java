package io.github.rabinarayanpatra.enumx;

import io.github.rabinarayanpatra.enumx.annotations.*;
import io.github.rabinarayanpatra.enumx.core.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for core classes.
 */
class CoreClassesTest {
    
    @Test
    void testEnumRegistry() {
        EnumRegistry registry = new EnumRegistry();
        
        // Register an enum
        registry.register(TestStatus.class);
        
        // Verify it's registered
        EnumMetadata metadata = registry.getByPath("test-status");
        assertThat(metadata).isNotNull();
        assertThat(metadata.getPath()).isEqualTo("test-status");
        assertThat(metadata.getKeyField()).isEqualTo("key");
        assertThat(metadata.isIncludeAllFields()).isFalse();
    }
    
    @Test
    void testEnumWithFields() {
        EnumRegistry registry = new EnumRegistry();
        registry.register(TestRole.class);
        
        EnumMetadata metadata = registry.getByPath("roles");
        assertThat(metadata).isNotNull();
        assertThat(metadata.getExposedFields()).hasSize(1);
        assertThat(metadata.getExposedFields().get(0).getApiName()).isEqualTo("displayName");
    }
    
    @EnumApi(path = "test-status")
    enum TestStatus {
        ACTIVE, INACTIVE
    }
    
    @EnumApi(path = "roles", includeAllFields = true)
    enum TestRole {
        ADMIN("Administrator", true),
        USER("User", false);
        
        @Expose("displayName")
        private final String label;
        
        @Hide
        private final boolean internal;
        
        TestRole(String label, boolean internal) {
            this.label = label;
            this.internal = internal;
        }
        
        public String getLabel() {
            return label;
        }
    }
}