package io.github.rabinarayanpatra.enumx.autoconfigure;

import io.github.rabinarayanpatra.enumx.core.EnumRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration that wires {@link EnumRegistry} and supporting infrastructure.
 */
@AutoConfiguration
@ConditionalOnClass(EnumRegistry.class)
@EnableConfigurationProperties(EnumxProperties.class)
public class EnumxAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumxAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public EnumRegistry enumRegistry() {
        LOGGER.debug("Creating default EnumRegistry bean");
        return new EnumRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumxRegistryInitializer enumxRegistryInitializer(EnumRegistry registry, ApplicationContext context) {
        return new EnumxRegistryInitializer(registry, context);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnBean(EnumRegistry.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "enumx.metadata", name = "enabled", havingValue = "true", matchIfMissing = true)
    public EnumxMetadataController enumxMetadataController(EnumRegistry registry) {
        return new EnumxMetadataController(registry);
    }
}
