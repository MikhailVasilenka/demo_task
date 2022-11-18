package com.nphase.service;

import com.nphase.entity.Category;
import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingCartService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public BigDecimal calculateTotalPrice(final ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateTotalPriceWithDiscount(final ShoppingCart shoppingCart, final BigDecimal discountPercent, final int discountApplyCount) {
        return shoppingCart.getProducts()
                .stream()
                .map(product ->
                        product.getQuantity() >= discountApplyCount ?
                                (product.getPricePerUnit()
                                        .subtract((product.getPricePerUnit()
                                                .multiply(discountPercent))
                                                .divide(ONE_HUNDRED)))
                                        .multiply(BigDecimal.valueOf(product.getQuantity())) :
                                product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateCategoryTotalPriceWithDiscount(final List<Product> products, final BigDecimal discountPercent, final int discountApplyCount) {
        final Map<Category, Integer> map = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.summingInt(Product::getQuantity)));
        return products.stream()
                .map(p -> {
                    if (map.containsKey(p.getCategory())) {
                        if (map.get(p.getCategory()) > discountApplyCount) {
                            return p.getPricePerUnit()
                                    .subtract((p.getPricePerUnit()
                                            .multiply(discountPercent))
                                            .divide(ONE_HUNDRED))
                                    .multiply(BigDecimal.valueOf(p.getQuantity()));
                        } else {
                            return p.getPricePerUnit().multiply(BigDecimal.valueOf(p.getQuantity()));
                        }
                    }
                    return BigDecimal.ZERO;
                }).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
