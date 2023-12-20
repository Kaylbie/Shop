package com.kursinis.prif4kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    String code;
    double price;
    String description;
    String photoName;
    double rating=0;
    int ratingCount=0;
    String manufacturer;
    boolean isVisible;
    @ManyToOne
    Warehouse warehouse;


    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Comment> comments;

    public Product(String title, String code, double price, String description, String photoName, String manufacturer, boolean isVisible) {
        this.title = title;
        this.code=code;
        this.price=price;
        this.description = description;
        this.photoName = photoName;
        this.manufacturer = manufacturer;
        this.isVisible = isVisible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
