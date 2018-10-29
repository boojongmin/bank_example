package boojongmin.bank

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.deserialize(json: String): T {
    return this.readValue(json, object: TypeReference<T>() {})
}
