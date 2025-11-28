package arch.cayenne.lib.common.utils.ext

/**
 * @date: 2025/11/21 14:40
 * @description:
 */
object CollectionExt {

    /**
     * C(n, k)的组合（适用于n<32)
     *
     * @param k: 取几个
     * @return
     *
     * eg：[A,B,C]的C(3,2): [A, B], [A, C], [B, C]
     */
    fun <T> List<T>.combinations(k:Int):List<List<T>>{
        return if(size < 32) combinationsBitmask(k) else combinationsIterator(k)
    }

    /**
     *  C(n, k)的组合——基于「二进制位掩码」的组合
     *
     * @param T
     * @param k
     * @return
     */
    private fun <T> List<T>.combinationsBitmask(k: Int): List<List<T>> {
        val n = size
        require(n <= 32) { "combinationsBitmask 不支持 n > 32,use combinationsIterator instead" } // 抛出异常
        if (k > n) return emptyList()
        if (k == 0) return listOf(emptyList())

        val result = mutableListOf<List<T>>()
        val total = 1 shl n

        for (mask in 0 until total) {
            if (mask.countOneBits() == k) {
                val combo = mutableListOf<T>()
                for (i in 0 until n) {
                    if ((mask shr i) and 1 == 1) combo.add(this[i])
                }
                result.add(combo)
            }
        }

        return result
    }

    /**
     * C(n, k)的组合——基于「索引组合指针」的经典 C(n,k) 生成算法
     *
     * @param T
     * @param k
     * @return
     */
    private fun <T> List<T>.combinationsIterator(k: Int): List<List<T>> {
        val n = size
        if (k > n) return emptyList()
        if (k == 0) return listOf(emptyList())

        val result = mutableListOf<List<T>>()
        val indices = IntArray(k) { it }  // 初始 [0,1,2,...,k-1]

        while (true) {
            // 收集当前组合
            result.add(indices.map { this[it] })

            // 找到可以“进位”的位置
            var i = k - 1
            while (i >= 0 && indices[i] == n - k + i) i--
            if (i < 0) break  // 到头了

            // 将该位 +1
            indices[i]++
            // 后面的全部顺延
            for (j in i + 1 until k) {
                indices[j] = indices[j - 1] + 1
            }
        }

        return result
    }

    /**
     * C(n, k)的组合——基于队列的层序构建
     *
     * @param T
     * @param k
     * @return
     */
    private fun <T> List<T>.combinationsQueue(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (k > size) return emptyList()

        var combos = listOf<List<T>>(emptyList())

        for (item in this) {
            combos = combos.flatMap { prev ->
                if (prev.size < k) {
                    listOf(prev, prev + item)
                } else {
                    listOf(prev)
                }
            }
        }

        return combos.filter { it.size == k }
    }


    /**
     * C(n, k)
     * eg:[A,B,C]取2-->[[A,B],[A,C],[B,C]]
     * @return
     */
    private fun <T> List<T>.combinationsRecursion(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (this.isEmpty()) return emptyList()

        val head = first()
        val tail = drop(1)

        val withHead = tail.combinations(k - 1).map { listOf(head) + it }
        val withoutHead = tail.combinations(k)

        return withHead + withoutHead
    }
}