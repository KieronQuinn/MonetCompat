package com.kieronquinn.monetcompat.extensions

import dev.kdrag0n.monet.theme.DynamicColorScheme

/**
 *  To avoid editing the core Monet code by kdrag0n, these are extensions instead
 */
fun DynamicColorScheme.isSameAs(other: Any?): Boolean {
    if(other !is DynamicColorScheme) return false
    this.accentColors.forEachIndexed { index, map ->
        if(!map.values.toList().deepEquals(other.accentColors[index].values.toList())) return false
    }
    this.neutralColors.forEachIndexed { index, map ->
        if(!map.values.toList().deepEquals(other.neutralColors[index].values.toList())) return false
    }
    return true
}