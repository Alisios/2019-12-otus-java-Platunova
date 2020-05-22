package ru.otus.api.cachehw;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Service
public class MyCache<K, V> implements HwCache<K, V> {
  final private Map<K, V> cache = new WeakHashMap<>();
  private final List<HwListener <K,V>> listeners = new ArrayList<>();

  @Override
  public void put(K key, V value) {
    cache.put(key, value);
    listeners.forEach(listener -> listener.notify(key, value, "put"));
  }

  public Map <K, V> getCache() {
    return cache;
  }

  @Override
  public void remove(K key) {
    V value = cache.remove(key);
    listeners.forEach(listener -> listener.notify(key, value, "remove"));
  }

  @Override
  public V get(K key) {
    return cache.get(key);
  }

  @Override
  public void addListener(HwListener<K, V> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(HwListener<K, V> listener) {
    listeners.remove(listener);
  }
}
