package io.github.rabinarayanpatra.enumx;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.CompilationSubject;
import io.github.rabinarayanpatra.enumx.processor.EnumxProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        CompilationSubject.assertThat(compilation).succeeded();
        CompilationSubject.assertThat(compilation)
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

        CompilationSubject.assertThat(compilation).succeeded();

        // Verify controller was generated
        CompilationSubject.assertThat(compilation)
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

        CompilationSubject.assertThat(compilation).succeeded();
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

        CompilationSubject.assertThat(compilation).succeeded();
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

        CompilationSubject.assertThat(compilation).failed();
        CompilationSubject.assertThat(compilation).hadErrorContaining("@EnumApi can only be applied to enums");
    }

    @Test
    void testFilteringLogicApplied() throws Exception {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.Product",
                """
                package com.example;

                import io.github.rabinarayanpatra.enumx.annotations.*;

                @EnumApi(path = "products", includeAllFields = true)
                public enum Product {
                    LAPTOP("Electronics", true, 1299, new java.math.BigDecimal("1299.00")),
                    BOOK("Education", false, 25, new java.math.BigDecimal("25.00"));

                    @Expose
                    @Filterable
                    private final String category;

                    @Filterable
                    private final boolean available;

                    @Filterable
                    private final int price;

                    @Filterable("msrp")
                    private final java.math.BigDecimal listPrice;

                    Product(String category, boolean available, int price, java.math.BigDecimal listPrice) {
                        this.category = category;
                        this.available = available;
                        this.price = price;
                        this.listPrice = listPrice;
                    }

                    public String getCategory() {
                        return category;
                    }

                    public boolean isAvailable() {
                        return available;
                    }

                    public int getPrice() {
                        return price;
                    }

                    public java.math.BigDecimal getListPrice() {
                        return listPrice;
                    }
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        CompilationSubject.assertThat(compilation).succeeded();

        JavaFileObject controllerClassFile = compilation.generatedFile(
                        StandardLocation.CLASS_OUTPUT,
                        "com.example.generated",
                        "ProductController.class")
                .orElseThrow(() -> new IllegalStateException("Generated controller bytecode not found"));
        JavaFileObject enumClassFile = compilation.generatedFile(
                        StandardLocation.CLASS_OUTPUT,
                        "com.example",
                        "Product.class")
                .orElseThrow(() -> new IllegalStateException("Compiled enum bytecode not found"));

        InMemoryClassLoader loader = new InMemoryClassLoader(Map.of(
                "com.example.generated.ProductController", readBytes(controllerClassFile),
                "com.example.Product", readBytes(enumClassFile)
        ));

        Class<?> controllerClass = loader.loadClass("com.example.generated.ProductController");
        Object controller = controllerClass.getConstructor().newInstance();
        Method getAll = controllerClass.getMethod("getAll", Map.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allResults = (List<Map<String, Object>>) getAll.invoke(controller, Map.of());
        assertThat(allResults).hasSize(2);

        Map<String, String> booleanFilter = Map.of("available", "true");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> availableResults = (List<Map<String, Object>>) getAll.invoke(controller, booleanFilter);
        assertThat(availableResults).hasSize(1);
        assertThat(availableResults.get(0).get("category")).isEqualTo("Electronics");

        Map<String, String> intFilter = Map.of("price", "25");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> priceResults = (List<Map<String, Object>>) getAll.invoke(controller, intFilter);
        assertThat(priceResults).hasSize(1);
        assertThat(priceResults.get(0).get("price")).isEqualTo(25);

        Map<String, String> bigDecimalFilter = Map.of("msrp", "1299.00");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> msrpResults = (List<Map<String, Object>>) getAll.invoke(controller, bigDecimalFilter);
        assertThat(msrpResults).hasSize(1);
        assertThat(msrpResults.get(0).get("category")).isEqualTo("Electronics");

        Map<String, String> unknownFilter = new LinkedHashMap<>();
        unknownFilter.put("unknown", "value");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> unknownResults = (List<Map<String, Object>>) getAll.invoke(controller, unknownFilter);
        assertThat(unknownResults).isEmpty();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/products").param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("LAPTOP"))
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        mockMvc.perform(get("/api/products").param("unknown", "value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/products/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"LAPTOP\", \"TABLET\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid[0]").value("LAPTOP"))
                .andExpect(jsonPath("$.invalid[0]").value("TABLET"));
    }

    @Test
    void testExposeFieldWithoutGetterFailsCompilation() {
        JavaFileObject enumFile = JavaFileObjects.forSourceString(
                "com.example.InvalidEnum",
                """
                package com.example;

                import io.github.rabinarayanpatra.enumx.annotations.*;

                @EnumApi(path = "invalid")
                public enum InvalidEnum {
                    SAMPLE;

                    @Expose
                    private final String label = "broken";
                }
                """
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new EnumxProcessor())
                .compile(enumFile);

        CompilationSubject.assertThat(compilation).failed();
        CompilationSubject.assertThat(compilation)
                .hadErrorContaining("must declare an accessible getter")
                .inFile(enumFile);
    }

    private static byte[] readBytes(JavaFileObject file) throws IOException {
        try (InputStream inputStream = file.openInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    private static final class InMemoryClassLoader extends ClassLoader {

        private final Map<String, byte[]> definitions;

        InMemoryClassLoader(Map<String, byte[]> definitions) {
            super(EnumxProcessorTest.class.getClassLoader());
            this.definitions = definitions;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = definitions.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
