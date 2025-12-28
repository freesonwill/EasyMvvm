package plugin.koin


import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * @author: zhangsan
 * @date: 2025/4/29 15:00
 * @description:
 */
class KoinViewModelProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    private val definitionWriter:DefinitionWriter by lazy { DefinitionWriter() }
    private val generatedPackage  by lazy {
        options["KOIN_GENERATED_PACKAGE"]
            ?: throw IllegalStateException("""
                ❌ "KOIN_GENERATED_PACKAGE" not found!
                ➡️ please config in the module's build.gradle：
                    ksp {
                        arg("KOIN_GENERATED_PACKAGE", "your.package.name")
                    }
                """.trimIndent())
    }
    private val defaultModule by lazy { "defaultModule" }
    private var createFileCount:Int = 0
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(KoinViewModel::class.java.name)
        val viewModels = symbols.filterIsInstance<KSClassDeclaration>().toList()
        val koinViewModelFiles = viewModels.mapNotNull { it.containingFile }
        logger.warn("Generating Koin ViewModel module...$generatedPackage,viewModels:${viewModels.size},koinViewModelFiles:${koinViewModelFiles.map { it.fileName }}")
        if(viewModels.isEmpty()) return emptyList()
        val fileName = defaultModule.replaceFirstChar { it.uppercaseChar() }
        //viewModels.isEmpty()第一次时创建空文件，第二次时返回，否则FileAlreadyExistsException
        if(createFileCount == 1 && viewModels.isEmpty()) return emptyList()
        createFileCount = (createFileCount+1) % 2
        val file = codeGenerator.createNewFile(
            /*// ❌ May cause file not to generate when referenced
            Dependencies(false),
            // ❌ Full aggregation hurts performance
            Dependencies.ALL_FILES,
            //✅ Correct: Only aggregate @KoinViewModel files for proper incremental build
            //Dependencies(aggregating = true, sources = koinViewModelFiles.toTypedArray()),*/
            if(viewModels.isEmpty()) //generate empty file
                Dependencies.ALL_FILES
            else
                Dependencies(aggregating = true, sources = koinViewModelFiles.toTypedArray()),
            generatedPackage,
            fileName
        )
        file.bufferedWriter().use { writer ->
            writer.write("package $generatedPackage\n\n")
            writer.write("import org.koin.dsl.module\n")
            if(viewModels.isNotEmpty()){
                writer.write("import org.koin.dsl.binds\n")
                writer.write("import org.koin.dsl.bind\n")
                writer.write("import org.koin.core.context.loadKoinModules\n")
                writer.write("import org.koin.androidx.viewmodel.dsl.viewModel\n\n")
            }

            writer.write("\ninternal val $defaultModule = module {\n")
            viewModels.forEach { classDeclaration ->
                val constructor = classDeclaration.primaryConstructor
                val paramCount = constructor?.parameters?.size ?: 0

                // Extract module argument from annotation, if present
                val moduleAnnotation = classDeclaration.annotations.find { it.shortName.asString() == "KoinViewModel" }
                if(moduleAnnotation == null) return@forEach
                var bindingsStr:String? = null
                var isGets:List<Boolean>? = null
                moduleAnnotation.arguments.forEach { it ->
                    when(it.name?.asString()){
                        "binds"->{
                            val bindings = (it.value as List<KSType>).map { it.declaration }
                            bindingsStr = definitionWriter.generateBindings(bindings)
                        }
                        "isGets"->{
                            isGets = it.value as List<Boolean>
                        }
                    }
                }
                //viewModel { (userId: String) -> arch.cayenne.module.home.ui.viewmodel.Test3ViewModel(userId, get()) }
                val constructorCall = buildString {
                    if(isGets != null && isGets!!.isNotEmpty()) {
                        append("(")
                        var i = 0
                        isGets!!.forEachIndexed { index, b ->
                            if(!b) {
                                if(i > 0) append(",")
                                val parameter = constructor?.parameters?.get(index)!!
                                append("${parameter.name?.asString()}:${parameter.type.resolve().declaration.qualifiedName?.asString()}")
                                i++
                            }
                        }
                        append(")->")
                    }
                    append("${classDeclaration.qualifiedName?.asString()}(")
                    repeat(paramCount) {index->
                        if(index > 0) append(", ")
                        if(isGets != null && isGets!!.getOrNull(index) == false){
                            val parameter = constructor?.parameters?.get(index)
                            append(parameter?.name?.asString())
                        }else {
                            append("get()")
                        }
                    }
                    append(")")
                }
                writer.write("    viewModel { $constructorCall } $bindingsStr \n")
            }
            writer.write("}\n")
        }

        return emptyList()
    }
}

class KoinViewModelProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KoinViewModelProcessor(environment.codeGenerator, environment.logger,environment.options)
    }
}