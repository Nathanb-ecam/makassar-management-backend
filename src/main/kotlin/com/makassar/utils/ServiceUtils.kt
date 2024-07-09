package com.makassar.utils

object ServiceUtils {
    fun mergeMapsByReplacingQuantity(existing: Map<String,String>?, new : Map<String,String>?): Map<String,String>? {
        if (new == null) {
            return existing
        }
        if(existing == null){
            return null
        }
        val merged = existing.toMutableMap()


        new.forEach{(itemId,quantity) ->
            if(merged.containsKey(itemId)) {
                merged[itemId] = quantity
            } else{
                merged.put(itemId, quantity)
            }
        }
        return merged
    }

}