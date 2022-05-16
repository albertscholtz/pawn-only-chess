fun containsKeyAndValue(map: Map<String, String>, value: String): Boolean {
    return map.containsValue(value) && map.contains(value)
}