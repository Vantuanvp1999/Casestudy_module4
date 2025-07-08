package com.codegym.casestudy.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob // For long text
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl; // Tên file ảnh
}