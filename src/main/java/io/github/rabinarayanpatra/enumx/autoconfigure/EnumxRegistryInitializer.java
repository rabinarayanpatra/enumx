package io.github.rabinarayanpatra.enumx.autoconfigure;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.core.EnumRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * Discovers {@link EnumApi} enums within the application base packages and registers them with the {@link EnumRegistry}.
 */
class EnumxRegistryInitializer implements SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumxRegistryInitializer.class);

    private final EnumRegistry registry;
    private final ApplicationContext applicationContext;

    EnumxRegistryInitializer(EnumRegistry registry, ApplicationContext applicationContext) {
        this.registry = registry;
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Set<Class<? extends Enum<?>>> enumTypes = scanForEnums();
        if (enumTypes.isEmpty()) {
            LOGGER.debug("EnumX auto-registration did not discover any @EnumApi enums");
            return;
        }

        enumTypes.forEach(registry::register);
        LOGGER.info("EnumX registered {} enum(s) from application classpath", enumTypes.size());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends Enum<?>>> scanForEnums() {
        if (!AutoConfigurationPackages.has(applicationContext)) {
            LOGGER.debug("AutoConfigurationPackages not available; skipping EnumX auto-registration");
            return Collections.emptySet();
        }

        List<String> basePackages = AutoConfigurationPackages.get(applicationContext);
        if (basePackages.isEmpty()) {
            LOGGER.debug("No base packages available for EnumX scanning");
            return Collections.emptySet();
        }

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EnumApi.class));

        ClassLoader classLoader = applicationContext.getClassLoader();
        Set<Class<? extends Enum<?>>> enumTypes = new LinkedHashSet<>();

        for (String basePackage : basePackages) {
            scanner.findCandidateComponents(basePackage).forEach(candidate -> {
                String className = candidate.getBeanClassName();
                try {
                    Class<?> candidateClass = ClassUtils.forName(className, classLoader);
                    if (!candidateClass.isEnum()) {
                        LOGGER.warn("Skipping @EnumApi type {} because it is not an enum", className);
                        return;
                    }
                    enumTypes.add((Class<? extends Enum<?>>) candidateClass);
                } catch (ClassNotFoundException ex) {
                    LOGGER.warn("Failed to load EnumX candidate {}: {}", className, ex.getMessage());
                }
            });
        }

        return enumTypes;
    }
}
