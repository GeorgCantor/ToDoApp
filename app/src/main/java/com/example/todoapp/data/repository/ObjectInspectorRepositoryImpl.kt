package com.example.todoapp.data.repository

import com.example.todoapp.data.objectinspector.core.ObjectAnalyzer
import com.example.todoapp.domain.model.InspectionNode
import com.example.todoapp.domain.repository.ObjectInspectorRepository

class ObjectInspectorRepositoryImpl(
    private val analyzer: ObjectAnalyzer,
) : ObjectInspectorRepository {
    private val cache = mutableMapOf<String, InspectionNode>()

    override fun inspect(
        obj: Any?,
        name: String,
    ) = analyzer.analyze(obj, name).apply { cacheNode(this) }

    override fun getNodeById(id: String) = cache[id]

    override fun clearCache() = cache.clear()

    private fun cacheNode(node: InspectionNode) {
        cache[node.id] = node
        node.children.forEach { cacheNode(it) }
    }
}
