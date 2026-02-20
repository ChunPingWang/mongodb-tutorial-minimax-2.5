package com.example.domain;

import java.time.LocalDate;
import java.util.Objects;

public record Applicant(
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    LocalDate dateOfBirth,
    String nationalId,
    String residenceAddress
) {

    public Applicant {
        Objects.requireNonNull(firstName, "First name is required");
        Objects.requireNonNull(lastName, "Last name is required");
        Objects.requireNonNull(email, "Email is required");
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        Objects.requireNonNull(dateOfBirth, "Date of birth is required");
        Objects.requireNonNull(nationalId, "National ID is required");
        Objects.requireNonNull(residenceAddress, "Residence address is required");
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    public int age() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isAdult() {
        return age() >= 18;
    }
}
