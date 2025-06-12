package com.example.tsb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Service implements Comparable<Service> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column
    String name;
    @Column
    int price;
    @Column
    String pathMainPhoto;
    @ElementCollection
    List<String> pathPhotos;
    @Column(length = 3000)
    String description;

    @OneToMany(mappedBy = "service")
    List<Review> reviews;

    @ManyToMany(mappedBy = "services")
    List<Schedule> schedules=new ArrayList<>();

    @Override
    public String toString() {
        return "Service{" +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || !(o instanceof Service)) return false;
        Service service=(Service) o;
        return this.id.equals(service.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public int compareTo(Service o) {

        return this.id.compareTo(o.getId());
    }
}
