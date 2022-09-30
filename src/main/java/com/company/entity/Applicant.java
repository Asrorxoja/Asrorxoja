package com.company.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Applicant {
    private String name;
    private String typeTechnology;
    private String username;
    private String phoneNumber;
    private String region;
    private String payment;
    private Integer age;
    private String profession;
    private String time;
    private String purpose;
    private String nameOffice;
    private String nameCharge;
    private String jobTime;
    private String salary;
}
