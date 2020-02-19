package collector.hotwheels.insight.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CacheProvider {

    private static Logger logger = LoggerFactory.getLogger(CacheProvider.class);

    private static collector.hotwheels.insight.util.CacheProvider instance;
    private Cache<String, Object> cache;
    private boolean saveNullValues = false;
    private int expiration = 3;

    private CacheProvider() {
        buildInstance();
    }

    private void buildInstance() {
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(expiration, TimeUnit.MINUTES)
                .build();
    }

    public static synchronized collector.hotwheels.insight.util.CacheProvider getInstance() {
        if (instance == null) {
            instance = new collector.hotwheels.insight.util.CacheProvider();
        }
        return instance;
    }

    public Object get(String key, Supplier<?> supplier) {

        Object cachedValue = cache.getIfPresent(key);

        if (cachedValue != null) {
            logger.debug("Cached value is " + cachedValue);
            return cachedValue;
        }
        Object supplierResult = null;
        try {
            supplierResult = cache.get(key, supplier::get);

            if (supplierResult != null || saveNullValues) {
                cachedValue = supplierResult;
            } else {
                cache.invalidate(key);
            }
        } catch (CacheLoader.InvalidCacheLoadException invex) {
            if (saveNullValues && supplierResult == null) {
                cache.put(key, supplierResult);
            }
        } catch (Exception ex) {
            logger.error("Error getting cache data key: " + key, ex);
        }

        return cachedValue;
    }

    public void setSaveNullValues(boolean saveNullValues) {
        if (this.saveNullValues != saveNullValues) {
            this.saveNullValues = saveNullValues;
            buildInstance();
        }
    }

    public void setExpiration(int expiration) {
        if (expiration >= 1 && this.expiration != expiration) {
            this.expiration = expiration;
            buildInstance();
        }
    }

    public void Clean() {
        try {
            cache.cleanUp();
        } catch (Exception ex) {
            logger.error("Error cleaning up the cache.", ex);
        }
    }

    public void Reset() {
        try {
            cache.invalidateAll();
        } catch (Exception ex) {
            logger.error("Error resetting the cache.", ex);
        }
    }
}
