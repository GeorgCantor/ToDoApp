package com.example.todoapp.data.objectinspector.core

import android.annotation.SuppressLint
import com.example.todoapp.domain.model.InspectionNode
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.IdentityHashMap

class ObjectAnalyzer {
    /**
     * Анализирует объект и строит дерево InspectionNode.
     *
     * @param obj Объект для анализа (может быть null)
     * @param name Имя объекта (для корневого обычно "Root")
     * @return InspectionNode - корень дерева
     */
    fun analyze(
        obj: Any?,
        name: String = "Root",
    ) = analyzeInternal(obj, name, IdentityHashMap())

    private fun analyzeInternal(
        obj: Any?,
        name: String,
        visited: IdentityHashMap<Any, String>,
    ): InspectionNode {
        if (obj == null) return InspectionNode.empty(name)

        visited[obj]?.let {
            return InspectionNode(
                id = IdGenerator.generateId(),
                name = name,
                type = obj.javaClass.simpleName,
                fullType = obj.javaClass.name,
                value = null,
                isPrimitive = false,
                isNull = false,
                modifiers = emptyList(),
                children = emptyList(),
                isRecursive = true,
                recursiveId = it,
            )
        }

        val objClass = obj.javaClass

        if (ValueFormatter.isPrimitiveOrSimple(objClass)) {
            return InspectionNode(
                id = IdGenerator.generateId(),
                name = name,
                type = objClass.simpleName,
                fullType = objClass.name,
                value = ValueFormatter.format(obj),
                isPrimitive = true,
                isNull = false,
            )
        }

        val currentNodeId = IdGenerator.generateId()
        val newVisited = IdentityHashMap(visited).apply { put(obj, currentNodeId) }

        val fields = getAllFields(objClass)
        val children = mutableListOf<InspectionNode>()

        for (field in fields) {
            try {
                field.isAccessible = true
                val fieldValue = field.get(obj)
                val modifiers = getModifiersList(field.modifiers)
                val typeInfo = getTypeInfo(field)
                val childNode =
                    analyzeInternal(fieldValue, field.name, newVisited).copy(
                        modifiers = modifiers,
                        fullType = typeInfo,
                    )
                children.add(childNode)
            } catch (e: Exception) {
                // Если не можем прочитать поле (например, SecurityManager блокирует)
                children.add(
                    InspectionNode(
                        id = IdGenerator.generateId(),
                        name = field.name,
                        type = field.type.simpleName,
                        fullType = "<error: ${e.message}>",
                        value = "<unreadable>",
                        isPrimitive = false,
                        isNull = false,
                        modifiers = getModifiersList(field.modifiers),
                    ),
                )
            }
        }

        return InspectionNode(
            id = IdGenerator.generateId(),
            name = name,
            type = objClass.simpleName,
            fullType = objClass.name,
            value = null,
            isPrimitive = false,
            isNull = false,
            modifiers = emptyList(),
            children = children.sortedBy { it.name },
        )
    }

    /**
     * Получает все поля класса, включая поля из родительских классов.
     *
     * @param clazz Класс для анализа
     * @return Список всех полей
     */
    private fun getAllFields(clazz: Class<*>) =
        buildList {
            var currentClass: Class<*>? = clazz
            while (currentClass != null && currentClass != Any::class.java) {
                addAll(currentClass.declaredFields)
                currentClass = currentClass.superclass
            }
        }

    /**
     * Преобразует битовую маску модификаторов в список строк.
     *
     * @param modifiers Битовая маска из Field.modifiers
     * @return Список строк (например, ["public", "final"])
     */
    private fun getModifiersList(modifiers: Int) =
        buildList {
            if (Modifier.isPublic(modifiers)) add("public")
            if (Modifier.isPrivate(modifiers)) add("private")
            if (Modifier.isProtected(modifiers)) add("protected")
            if (Modifier.isStatic(modifiers)) add("static")
            if (Modifier.isFinal(modifiers)) add("final")
            if (Modifier.isVolatile(modifiers)) add("volatile")
            if (Modifier.isTransient(modifiers)) add("transient")
            if (Modifier.isSynchronized(modifiers)) add("synchronized")
        }

    /**
     * Получает строковое представление типа поля с учетом дженериков.
     *
     * @param field Поле для анализа
     * @return Строка с информацией о типе
     */
    @SuppressLint("NewApi")
    private fun getTypeInfo(field: Field) =
        when (val genericType = field.genericType) {
            is ParameterizedType -> {
                val rawType = (genericType.rawType as Class<*>).simpleName
                val typeArgs =
                    genericType.actualTypeArguments.joinToString {
                        if (it is Class<*>) it.simpleName else it.typeName
                    }
                "$rawType<$typeArgs>"
            }
            is Class<*> -> genericType.simpleName
            else -> genericType.typeName
        }
}
