package com.example.tsb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @Column
    byte stars;
    @Column
    String name;
    @ManyToOne
    Service service;
    @Column(length = 3000)
    String reviewText;
    @Column
    boolean confirmation=false;
    @Column
    LocalDateTime date=LocalDateTime.now();
    @ElementCollection()
    List<String> pathImages;
    @ManyToOne
    User user;

    public Review(byte stars, String name, List<String> pathImages, String reviewText, User user) {
        this.stars = stars;
        this.name = name;
        this.reviewText = reviewText;
        this.pathImages=pathImages;

        this.user=user;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", stars=" + stars +
                ", name='" + name + '\'' +
                ", service=" + service +
                ", reviewText='" + reviewText + '\'' +
                ", confirmation=" + confirmation +
                ", date=" + date +
                ", pathImages=" + pathImages +
                ", user=" + user +
                '}';
    }
}
