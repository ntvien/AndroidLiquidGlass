package com.kyant.backdrop

import org.intellij.lang.annotations.Language

sealed interface RuntimeShaderCache {

    fun obtainRuntimeShader(key: String, @Language("AGSL") string: String): RuntimeShader
}

/**
 * Shared by all backdrop, highlight and effect nodes, so each shader is compiled once instead of once per node.
 *
 * Sharing is safe because callers set the uniforms and create the effect or record the paint right away, and the
 * effect or recorded shader keeps a copy of the uniforms.
 */
internal object RuntimeShaderCacheImpl : RuntimeShaderCache {

    private val runtimeShaders = mutableMapOf<String, Pair<String, RuntimeShader>>()

    override fun obtainRuntimeShader(key: String, string: String): RuntimeShader {
        val cached = runtimeShaders[key]
        if (cached != null && cached.first == string) {
            return cached.second
        }
        return RuntimeShader(string).also { runtimeShaders[key] = string to it }
    }
}
