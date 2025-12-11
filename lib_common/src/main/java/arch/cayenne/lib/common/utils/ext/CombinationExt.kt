package arch.cayenne.lib.common.utils.ext

/**
 * @date: 2025/11/21 14:40
 * @description:
 */
object CombinationExt {

    /**
     * C(n, k)的组合（适用于n<32)
     *
     * @param k: 取几个
     * @return
     *
     * eg：[A,B,C]的C(3,2): [A, B], [A, C], [B, C]
     */
    fun <T> List<T>.combinations(k:Int):List<List<T>>{
        //"aaaa---combinations--k:$k,listSize:${this.size}".printStackTrace()
        return if(size < 32) combinationsBitmask(k) else combinationsIterator(k)
    }

    /**
     * 计算 n 选 r 的组合数
     * C(n, k) = n! / (k! × (n-k)!)
     */
    fun cNK(n:Int,k: Int): Int {
        require(n<34){ "C($n,$k) too large for Int" } //
        if (k < 0 || k > n) return 0
        var result = 1
        for (i in 1..k) {
            result = result * (n - k + i) / i
        }
        return result
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
        /*val t1 = System.currentTimeMillis()
        "combinationsBitmask begin:${size}/$k".logd()*/
        for (mask in 0 until total) {
            if (mask.countOneBits() == k) {
                val combo = mutableListOf<T>()
                for (i in 0 until n) {
                    if ((mask shr i) and 1 == 1) combo.add(this[i])
                }
                result.add(combo)
            }
        }
        /*val t2 = System.currentTimeMillis()
        val costMils = t2 - t1
        "combinationsBitmask end:${size}/$k,list:${result.size},costMils:$costMils".let {
            if(costMils > 50) it.loge() else it.logd()
        }*/
        return result
    }

    /**
     * 获取第 m 个组合（1-based）按字典序
     * list: 原始元素列表，必须按顺序排列
     * k: 组合长度
     * m: 第 m 个组合
     */
    fun <T> List<T>.kthCombination(k: Int, m: Long): List<T> {
        val list = this
        val n = list.size
        require(k in 0..n) { "Invalid combination length k=$k for list of size $n" }
        val total = cNK(n,k)
        require(m in 0 until total) { "m=$m out of range, total combinations=$total" }
        val result = mutableListOf<T>()
        var remaining = k
        var target = m + 1 // 改这里：0-based -> 1-based

        var start = 0
        while (remaining > 0) {
            val count = cNK(n - start - 1,remaining - 1)
            if (target <= count) {
                result.add(list[start])
                remaining--
            } else {
                target -= count
            }
            start++
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