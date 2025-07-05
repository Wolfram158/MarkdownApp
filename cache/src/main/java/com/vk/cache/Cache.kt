package com.vk.cache

interface Cache<K : Any, V : Any> {
    fun get(key: K): V?

    fun put(key: K, value: V)
}