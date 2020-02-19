package collector.hotwheels.insight.imports.manager;

import collector.hotwheels.api.client.WikiaClient;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.util.CacheProvider;
import collector.hotwheels.insight.util.rest.EndPointSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public enum ClientProvider {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(ClientProvider.class);

    public synchronized WikiaClient getClient(ImportConfiguration configuration) {

        WikiaClient result = (WikiaClient) CacheProvider.getInstance()
                .get(String.valueOf(configuration.hashCode()), new ConfigurationLoader(configuration));

        return result;
    }

    public synchronized boolean validClient(ImportConfiguration configuration) {
        boolean ret = false;
        try {
            WikiaClient wikiaClient = getClient(configuration);
            if (wikiaClient != null) {
                ret = wikiaClient.isValidClient();
            }
        } catch (Exception ex) {
            ret = false;
            logger.error("Error validating client.", ex);
        }
        return ret;
    }

    public static class ConfigurationLoader implements Supplier<WikiaClient> {

        private final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

        private EndPointSettings endPointSettings;
        private final ImportConfiguration configuration;

        public ConfigurationLoader(ImportConfiguration configuration) {
            this.configuration = configuration;

        }

        @Override
        public WikiaClient get() {
            WikiaClient wikiaClient = null;
            try {
                setEndPointSettings();

                wikiaClient = new WikiaClient.Builder(configuration.getDataFolder(), endPointSettings).setRequestLimit(
                        configuration.getRequestLimit())
                        .setDownloadImages(configuration.getImportImages())
                        .build();

            } catch (Exception ex) {
                logger.error("Error trying to authenticate client.", ex);
            }
            return wikiaClient;
        }

        private void setEndPointSettings() {
            if (configuration != null) {
                endPointSettings = new EndPointSettings.Builder().setEndpoint(configuration.getBaseApiEndpoint())
                        .build();
            }
        }

    }
}
