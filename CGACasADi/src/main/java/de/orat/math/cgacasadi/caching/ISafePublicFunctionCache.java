package de.orat.math.cgacasadi.caching;

import java.util.Map;
import java.util.SortedMap;

public interface ISafePublicFunctionCache {

    void clearCache();

    Map<String, Integer> getUnmodifiableCachedFunctionsUsage();

    SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage();

    String cachedFunctionUsageToString();
}
