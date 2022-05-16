fun bill(priceList: Map<String, Int>, shoppingList: MutableList<String>): Int {
    return priceList.entries.sumOf { (k,v) -> if (shoppingList.contains(k)) v else 0 }
}