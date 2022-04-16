package com.example.demo.Entity.Comparators;

import com.example.demo.Entity.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        Integer price1 = Integer.parseInt(o1.getPrice());
        Integer price2 = Integer.parseInt(o2.getPrice());
        return price1.compareTo(price2);
    }
}
