package com.bitsfromspace.moneytracker.utils;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author chris
 * @since 04-11-15.
 */
public class Cache <K, V> {

    public interface Delegate<K, V>{
        V get(K key);
    }

    private final TimeProvider timeProvider;
    private final Delegate<K, V> delegate;
    private final long retentionMillis;
    private final Map<K, V> keyToValue;
    private final TObjectLongMap<K> keyToAge;
    private final ReadWriteLock rwLock;

    public Cache(TimeProvider timeProvider, long retentionMillis, Delegate<K, V> delegate) {
        this.timeProvider = timeProvider;
        this.retentionMillis = retentionMillis;
        this.delegate = delegate;
        keyToValue = new HashMap<>();
        keyToAge = new TObjectLongHashMap<>();
        rwLock = new ReentrantReadWriteLock();
    }

    public V get(K key){
        rwLock.readLock().lock();
        try {
            if (timeProvider.getTime() - keyToAge.get(key) < retentionMillis){
                return keyToValue.get(key);
            }
        } finally {
            rwLock.readLock().unlock();
        }
        rwLock.writeLock().lock();
        try {
            if (timeProvider.getTime() - keyToAge.get(key) < retentionMillis) {
                return keyToValue.get(key);
            }
            V value = delegate.get(key);
            keyToValue.put(key, value);
            keyToAge.put(key, timeProvider.getTime());
            return value;
        } finally {
            rwLock.writeLock().unlock();
        }
    }


}
