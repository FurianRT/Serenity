package com.furianrt.core

fun CharSequence.indexOfLastOrNull(predicate: (Char) -> Boolean): Int? =
    indexOfLast(predicate).takeIf { it != -1 }

fun CharSequence.indexOfFirstOrNull(predicate: (Char) -> Boolean): Int? =
    indexOfFirst(predicate).takeIf { it != -1 }