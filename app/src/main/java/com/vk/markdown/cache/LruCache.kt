package com.vk.markdown.cache

class LruCache<K : Any, V : Any>(private val capacity: Int) : Cache<K, V> {
    private class Node<K, V>(var key: K?, var value: V?) {
        constructor() : this(null, null)

        var prev: Node<K, V>? = null
        var next: Node<K, V>? = null
    }

    private val data = HashMap<K, Node<K, V>>()
    private var volume = 0
    private var head = Node<K, V>()
    private var last = Node<K, V>()
        .apply {
            prev = head
            head.next = this
        }

    private fun removeNode(node: Node<K, V>) {
        node.next?.prev = node.prev
        node.prev?.next = node.next
    }

    private fun addNode(node: Node<K, V>) {
        node.next = head.next
        head.next = node
        node.prev = head
        node.next?.prev = node
    }

    @Synchronized
    override fun get(key: K): V? {
        if (!data.contains(key)) {
            return null
        }
        val node = data[key]!!
        removeNode(node)
        addNode(node)
        return node.value!!
    }

    @Synchronized
    override fun put(key: K, value: V) {
        if (data.contains(key)) {
            data[key]?.value = value
            removeNode(data[key]!!)
            addNode(data[key]!!)
            return
        }
        val node = Node(key, value)
        when (volume) {
            0 -> {
                data[key] = node
                addNode(node)
                last.prev?.next = node
                node.prev = last.prev
                last.prev = node
                node.next = last
                volume++
            }

            capacity -> {
                data[key] = node
                data.remove(last.prev!!.key!!)
                removeNode(last.prev!!)
                addNode(node)
            }

            else -> {
                volume++
                data[key] = node
                addNode(node)
            }

        }
    }

}