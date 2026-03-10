package com.example.todoapp.data.objectinspector.core

/**
 * Узел дерева инспекции объектов.
 *
 * @param id Уникальный идентификатор узла (для навигации и отслеживания циклов)
 * @param name Имя поля или "Root" для корневого объекта
 * @param type Простое имя типа (String, Int, Person...)
 * @param fullType Полное имя типа с дженериками (List<String>, Map<String, Int>...)
 * @param value Строковое представление значения (для примитивов и простых объектов)
 * @param isPrimitive true если это примитив, строка, число или другой "простой" тип
 * @param isNull true если значение null
 * @param modifiers Список модификаторов (public, private, final, etc.)
 * @param children Дочерние узлы (для сложных объектов)
 * @param isRecursive true если это циклическая ссылка на уже показанный объект
 * @param recursiveId ID узла, на который ссылаемся (если isRecursive = true)
 */
data class InspectionNode(
    val id: String,
    val name: String,
    val type: String,
    val fullType: String,
    val value: String?,
    val isPrimitive: Boolean,
    val isNull: Boolean,
    val modifiers: List<String> = emptyList(),
    val children: List<InspectionNode> = emptyList(),
    val isRecursive: Boolean = false,
    val recursiveId: String? = null,
) {
    val hasChildren: Boolean
        get() = children.isNotEmpty()

    companion object {
        fun empty(name: String) =
            InspectionNode(
                id = IdGenerator.generateId(),
                name = name,
                type = "null",
                fullType = "null",
                value = "null",
                isPrimitive = true,
                isNull = true,
                modifiers = emptyList(),
            )
    }
}
