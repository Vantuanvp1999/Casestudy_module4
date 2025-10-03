package com.codegym.casestudy.service;

import com.codegym.casestudy.entity.Product;
import com.codegym.casestudy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Path root;

    public ProductService(ProductRepository productRepository, @Value("${file.upload-dir}") String uploadPath) {
        this.productRepository = productRepository;
        this.root = Paths.get(uploadPath);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            // Tạo tên file duy nhất
            String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            // Lưu file vào thư mục
            Files.copy(imageFile.getInputStream(), this.root.resolve(filename));
            // Lưu tên file vào product
            product.setImageUrl(filename);
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
