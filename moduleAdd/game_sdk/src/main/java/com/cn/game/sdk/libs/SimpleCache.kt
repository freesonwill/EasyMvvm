package com.cn.game.sdk.libs

class SimpleCache<K, V> {
    private val cacheMap: MutableMap<K, V> = HashMap()
    private val maxSize: Int

    // 构造函数，可以设定缓存的最大大小
    constructor(maxSize: Int) {
        this.maxSize = maxSize
    }

    // 获取缓存中的数据
    fun get(key: K): V? {
        return cacheMap[key]
    }

    // 将数据放入缓存中
    fun put(key: K, value: V) {
        if (cacheMap.size >= maxSize) {
            // 如果缓存已满，可以选择移除最近最少使用的元素，这里简单移除第一个元素作为示例
            cacheMap.remove(cacheMap.keys.firstOrNull())
        }
        cacheMap[key] = value
    }

    // 从缓存中移除数据
    fun remove(key: K) {
        cacheMap.remove(key)
    }

    // 清除所有缓存数据
    fun clear() {
        cacheMap.clear()
    }

    // 获取当前缓存的大小
    fun size(): Int {
        return cacheMap.size
    }
}