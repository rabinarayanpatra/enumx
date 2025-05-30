package io.github.rabinarayanpatra.enumx;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import io.github.rabinarayanpatra.enumx.processor.EnumxProcessor;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

/**
 * Tests for the EnumX annotation processor.
 */
class EnumxProcessorTest {

    @Test
    void testSimpleEnumGeneration() {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.Status",
                """
                package com.example;
                
                import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
                
                @EnumApi(path = "status")
                public enum Status {
                    ACTIVE, INACTIVE, PENDING;
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.example.generated.StatusController");
    }

    @Test
    void testEnumWithFields() {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.Role",
                """
                package com.example;
                
                import io.github.rabinarayanpatra.enumx.annotations.*;
                
                @EnumApi(path = "roles", includeAllFields = true)
                public enum Role {
                    ADMIN("Administrator", true),
                    USER("Regular User", false);
                    
                    @Expose("displayName")
                    private final String label;
                    
                    @Hide
                    private final boolean internal;
                    
                    Role(String label, boolean internal) {
                        this.label = label;
                        this.internal = internal;
                    }
                    
                    public String getLabel() {
                        return label;
                    }
                    
                    public boolean isInternal() {
                        return internal;
                    }
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        assertThat(compilation).succeeded();

        // Verify controller was generated
        assertThat(compilation)
                .generatedSourceFile("com.example.generated.RoleController");

        // Could add more specific assertions about the generated content
    }

    @Test
    void testEnumWithCustomKeyField() {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.Country",
                """
                package com.example;
                
                import io.github.rabinarayanpatra.enumx.annotations.*;
                
                @EnumApi(path = "countries", keyField = "code")
                public enum Country {
                    USA("United States"),
                    UK("United Kingdom");
                    
                    @Expose
                    private final String name;
                    
                    Country(String name) {
                        this.name = name;
                    }
                    
                    public String getName() {
                        return name;
                    }
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        assertThat(compilation).succeeded();
    }

    @Test
    void testFilterableFields() {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.Product",
                """
                package com.example;
                
                import io.github.rabinarayanpatra.enumx.annotations.*;
                
                @EnumApi(path = "products")
                public enum Product {
                    LAPTOP("Electronics", true),
                    BOOK("Education", false);
                    
                    @Expose
                    @Filterable("category")
                    private final String category;
                    
                    @Expose
                    @Filterable
                    private final boolean available;
                    
                    Product(String category, boolean available) {
                        this.category = category;
                        this.available = available;
                    }
                    
                    public String getCategory() {
                        return category;
                    }
                    
                    public boolean isAvailable() {
                        return available;
                    }
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        assertThat(compilation).succeeded();
    }

    @Test
    void testErrorOnNonEnum() {
        JavaFileObject classFile = JavaFileObjects.forSourceString(
                "com.example.NotAnEnum",
                """
                package com.example;
                
                import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
                
                @EnumApi(path = "invalid")
                public class NotAnEnum {
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(classFile);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("@EnumApi can only be applied to enums");
    }
}