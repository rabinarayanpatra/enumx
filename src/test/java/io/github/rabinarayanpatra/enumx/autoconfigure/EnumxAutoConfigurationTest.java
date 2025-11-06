package io.github.rabinarayanpatra.enumx.autoconfigure;

import io.github.rabinarayanpatra.enumx.core.EnumMetadata;
import io.github.rabinarayanpatra.enumx.core.EnumRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnumxAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(context -> AutoConfigurationPackages.register(
                    (BeanDefinitionRegistry) context.getBeanFactory(),
                    "io.github.rabinarayanpatra.enumx.autoconfigure.samples"))
            .withConfiguration(AutoConfigurations.of(EnumxAutoConfiguration.class));

    @Test
    void autoRegistersAnnotatedEnums() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EnumRegistry.class);
            EnumRegistry registry = context.getBean(EnumRegistry.class);
            EnumMetadata metadata = registry.getByPath("alpha-values");
            assertThat(metadata).isNotNull();
            assertThat(metadata.getEnumClass().getSimpleName()).isEqualTo("AlphaEnum");
        });
    }

    @Test
    void metadataControllerReturnsViewModel() {
        contextRunner.run(context -> {
            EnumxMetadataController controller = context.getBean(EnumxMetadataController.class);
            List<EnumxMetadataController.EnumMetadataView> all = controller.getAll();
            assertThat(all).isNotEmpty();
            assertThat(all.stream().anyMatch(view -> view.path().equals("alpha-values"))).isTrue();
        });
    }

    @Test
    void metadataControllerCanBeDisabled() {
        contextRunner.withPropertyValues("enumx.metadata.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(EnumxMetadataController.class));
    }
}
