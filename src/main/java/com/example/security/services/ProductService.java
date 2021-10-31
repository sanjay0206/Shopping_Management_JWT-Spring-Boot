package com.example.security.services;

import com.example.security.entities.AppUser;
import com.example.security.entities.Category;
import com.example.security.entities.Product;
import com.example.security.repositories.AppUserRepo;
import com.example.security.repositories.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final AppUserRepo appUserRepo;

    @Autowired
    public ProductService(ProductRepo productRepo, AppUserRepo appUserRepo) {
        this.productRepo = productRepo;
        this.appUserRepo = appUserRepo;
    }

    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Product> products = productRepo.findAll();
        products.forEach(product -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", product.getId());
            map.put("productName", product.getProductName());
            map.put("productCategory", product.getProductCategory());
            map.put("productPrice", product.getProductPrice());
            list.add(map);
        });
        return list;
    }

    public String addProduct(Map<String, Object> request) {
        String productName = (String) request.get("productName");
        Category productCategory = Category.valueOf((String) request.get("productCategory"));
        Double productPrice = (Double) request.get("productPrice");
        long vendorId = (long) request.get("vendor_id");
        Optional<AppUser> vendor = appUserRepo.findById(vendorId);
        if (!vendor.isPresent()) {
            return "Vendor with id " +  vendorId + " is not found";
        }
        Product product = new Product();
        product.setProductPrice(productPrice);
        product.setProductName(productName);
        product.setProductCategory(productCategory);
        product.setAppUser(vendor.get());
        productRepo.save(product);
        return "Success";
    }

    public String deleteProduct(Long productId) {
        Optional<Product> product = productRepo.findById(productId);
        if (!product.isPresent()) {
            return "Product with id " + productId + " is not found";
        }
        productRepo.deleteById(productId);
        log.info("Product with id {} is deleted", productId);
        return "Product with id " + productId + " is deleted";
    }

    @Transactional
    public String updateProduct(Map<String, Object> request) {
        try {
            String productIdObj = request.get("user_id").toString();
            if (productIdObj ==  null) {
                return "Please provide the product id to update";
            }
            String productName = request.get("productName") == null ? "NA" :
                    (request.get("productName").equals("")) ? "NA" : request.get("productName").toString();
            String productPriceStr = request.get("productPrice") == null ? "NA" :
                    (request.get("productPrice").equals("")) ? "NA" : request.get("productPrice").toString();
            long productPrice = Long.parseLong(productPriceStr);
            long productId = Long.parseLong(productIdObj);

            Optional<Product> product = productRepo.findById(productId);
            if (!product.isPresent()) {
                return "Product with id " + productId + " is not present";
            } else  {
                String existingProductName = product.get().getProductName();
                if (!productName.equals("NA") && !Objects.equals(productName, existingProductName)) {
                    Optional<Product> productOptional =
                            productRepo.findByProductName(productName);
                    if(productOptional.isPresent()) {
                        return "Product name is already exists";
                    }
                    product.get().setProductName(productName);
                }

                double existingProductPrice = product.get().getProductPrice();
                if (!productPriceStr.equals("NA") && !Objects.equals(productPriceStr, existingProductPrice)) {
                    product.get().setProductPrice((double) productPrice);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Product details has been updated";
    }
}
