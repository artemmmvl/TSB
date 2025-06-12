package com.example.tsb.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Column
    private String email;
    @ManyToOne
    private Service service;
    @Column
    private String phoneNumber;
    @Column
    private String inst;
    @Column
    private boolean actual=true;
    @Column
    private boolean permissionToSendEmail;
    @Column
    private boolean emailSend=false;
    @OneToOne
    private Schedule schedule;
    @Column
    private String comment;
    @ManyToOne
    private User user;
    private LocalDateTime recordingTime;

    public Appointment(String name, String email,String inst, Service service, String phoneNumber, Schedule schedule,String comment, boolean permissionToSendEmail) {
        this.name = name;
        this.email = email;
        this.service = service;
        this.inst=inst;
        this.phoneNumber = phoneNumber;
        this.schedule = schedule;
        this.comment=comment;
        this.permissionToSendEmail=  permissionToSendEmail==true;
        this.recordingTime=LocalDateTime.now();

    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", service=" + service+
                ", phoneNumber='" + phoneNumber + '\'' +
                ", inst='" + inst + '\'' +
                ", actual=" + actual +
                ", permissionToSendEmail=" + permissionToSendEmail +
                ", emailSend=" + emailSend +
                ", schedule=" + schedule +
                ", comment='" + comment + '\'' +
                ", recordingTime=" + recordingTime +
                '}';
    }
}
