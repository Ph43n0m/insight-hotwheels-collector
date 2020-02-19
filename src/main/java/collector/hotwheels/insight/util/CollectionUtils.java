package collector.hotwheels.insight.util;

import org.apache.commons.collections.TransformerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class CollectionUtils {

    public static <T> T pickValues(Collection input, String fieldName) {
        HashSet<T> ret = new HashSet<>();

        if (input != null && !input.isEmpty()) {
            ret.addAll(new ArrayList(org.apache.commons.collections.CollectionUtils.collect(input,
                    TransformerUtils.invokerTransformer(fieldName))));
        }
        ret.removeAll(Collections.singleton(null));
        return (T) new ArrayList(ret);
    }
}
