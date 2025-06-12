package com.example.tsb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Schedule implements Comparable<Schedule> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @Column
    LocalDateTime date;
    @Column
    boolean booked=false;
    @OneToOne(mappedBy = "schedule")
    Appointment appointment;
    @ManyToMany
    List<Service> services=new ArrayList<>();
    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", date=" + date +
                ", booked=" + booked +

                '}';
    }
    public Schedule(LocalDateTime date, List<Service> services) {
        this.date = date;
        this.services = services;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return booked == schedule.booked && Objects.equals(date, schedule.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date,booked);
    }
    @Override
    public int compareTo(Schedule o) {
        return this.date.compareTo(o.date);
    }
}
