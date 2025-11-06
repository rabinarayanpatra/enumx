package io.github.rabinarayanpatra.enumx.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "enumx")
public class EnumxProperties {

    private final Metadata metadata = new Metadata();

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata {
        /**
         * Enables the diagnostic metadata REST endpoint (`/enumx/metadata`).
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
