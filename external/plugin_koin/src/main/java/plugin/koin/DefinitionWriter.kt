package plugin.koin

import com.google.devtools.ksp.symbol.KSDeclaration

/**
 * @author: zhangsan
 * @date: 2025/4/30 16:13
 * @description:
 */
class DefinitionWriter {
    private val BLOCKED_TYPES = listOf("Any", "ViewModel", "CoroutineWorker", "ListenableWorker")

    fun generateBindings(bindings: List<KSDeclaration>): String {
        val validBindings = bindings.filter {
            val clazzName = it.simpleName.asString()
            clazzName !in BLOCKED_TYPES
        }

        return when {
            validBindings.isEmpty() -> ""
            validBindings.size == 1 -> {
                val generateBinding = generateBinding(validBindings.first())
                "bind($generateBinding)"
            }

            else -> validBindings.joinToString(
                prefix = "binds(arrayOf(",
                separator = ",",
                postfix = "))"
            ) {
                generateBinding(it)
            }
        }
    }

    private fun generateBinding(declaration: KSDeclaration): String {
        val packageName = declaration.packageName.asString().filterForbiddenKeywords()
        val className = declaration.simpleName.asString()
        val parents = getParentDeclarations(declaration)
        return if (parents.isNotEmpty()) {
            val parentNames = parents.joinToString(".") { it.simpleName.asString() }
            "$packageName.$parentNames.$className::class"
        } else {
            "$packageName.$className::class"
        }
    }

    private fun getParentDeclarations(declaration: KSDeclaration): List<KSDeclaration> {
        val parents = mutableListOf<KSDeclaration>()

        var parent = declaration.parentDeclaration
        while (parent != null) {
            parents.add(parent)
            parent = parent.parentDeclaration
        }

        return parents.reversed()
    }
}