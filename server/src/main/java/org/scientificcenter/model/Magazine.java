package org.scientificcenter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Magazine implements Serializable {

    private static final long serialVersionUID = 4786224022012986960L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Length(min = 8, max = 8)
    private String issn;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType payment;

    @Column
    private Boolean enabled;

    @Column
    private Boolean chosenEditorsAndReviewers;

    @Column
    private Boolean requestedChanges;

    @Column(unique = true)
    private String merchantId;

    @Column
    private Boolean enabledAsMerchant;

    @Column
    private Double membershipPrice;

    @Column
    private String membershipCurrency;

    @ManyToOne(optional = false)
    private User mainEditor;

    @ManyToMany
    private Set<User> editors;

    @ManyToMany
    private Set<User> reviewers;

    @ManyToMany
    private Set<ScientificArea> scientificAreas;

    @OneToMany(mappedBy = "magazine")
    private Set<Payment> membershipPayments;

    @OneToMany(mappedBy = "magazine")
    private Set<Subscription> subscriptions;
}