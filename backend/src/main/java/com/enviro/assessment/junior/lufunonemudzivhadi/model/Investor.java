package com.enviro.assessment.junior.lufunonemudzivhadi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "investors")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Investor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate dateOfBirth;
    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvestmentProduct> investmentProducts;
}
