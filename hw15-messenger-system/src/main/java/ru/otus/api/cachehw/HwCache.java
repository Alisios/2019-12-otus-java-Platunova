package ru.otus.api.cachehw;

import java.util.Map;

public interface HwCache<K, V> {

    void put(K key, V value);

    void remove(K key);

    V get(K key);

    Map<K, V> getCache();

    void addListener(HwListener<K, V> listener);

    void removeListener(HwListener<K, V> listener);
}
