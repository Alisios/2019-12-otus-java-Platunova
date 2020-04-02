package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** класс для демонстрации работы кэша**/
public class HWCacheDemo {
  private static final Logger logger = LoggerFactory.getLogger(HWCacheDemo.class);

  public static void main(String[] args) {
    new HWCacheDemo().demo();
  }

  private void demo() {
    HwCache<Integer, Integer> cache = new MyCache<>();
    HwCache<String, String> cache2 = new MyCache<>();

    HwListener<Integer, Integer> listener = new HwListener<Integer, Integer>() {
      @Override
      public void notify(Integer key, Integer value, String action) {
        logger.info("key:{}, value:{}, action: {}", key, value, action);
      }
    };

    HwListener<String, String> listener2 = new HwListener<String, String>() {
      @Override
      public void notify(String key, String value, String action) {
        logger.info("key:{}, value:{}, action: {}", key, value, action);
      }
    };

    cache2.addListener(listener2);
    cache2.put("Женя", "Москва");
    cache2.put("Саша", "Питер");
    cache2.put("Рома", "Лондон");

    logger.info("getValue:{}", cache2.get("Женя"));
    cache2.remove("Саша");
    cache2.removeListener(listener2);

    cache.addListener(listener);
    cache.put(1, 1);

    logger.info("getValue:{}", cache.get(1));
    cache.remove(1);
    cache.removeListener(listener);
  }
}
