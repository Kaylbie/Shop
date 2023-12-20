package com.kursinis.prif4kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String commentTitle;
    private String commentBody;
    private LocalDate dateCreated;

    @ManyToOne
    private Comment parentComment;

    // One-to-many relationship for replies
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.EAGER)
    private List<Comment> replies;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

    // Constructors, getters, setters...

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public Comment(User user, String commentTitle, String commentBody, Product product) {
        this.user=user;
        this.commentTitle = commentTitle;
        this.commentBody = commentBody;
        this.dateCreated = LocalDate.now();
        this.product=product;
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    @Override
    public String toString() {
        return user.getName()+" wrote "+commentTitle + " on " + dateCreated;
    }
}
