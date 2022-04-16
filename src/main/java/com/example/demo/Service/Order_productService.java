package com.example.demo.Service;

import com.example.demo.Entity.Order;
import com.example.demo.Entity.Order_product;
import com.example.demo.Entity.Product;
import com.example.demo.Repositories.Order_productRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Order_productService {

    @Autowired
    Order_productRepository order_productRepository;


    /**
     * Сохранение order_product (это один из пунктов в целом заказе)
     * @param order Заказ, к которому принадлежит эта запись
     * @param product Продукт, который пытаются приобрести
     * @param amount количество Продуктов
     */
    public void save(Order order, Product product, long amount) {

        Order_product order_product = new Order_product();
        order_product.setOrder(order);
        order_product.setProduct(product);
        order_product.setAmountOfProduct(amount);

        order_productRepository.save(order_product);
    }



}
