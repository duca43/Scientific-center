package org.scientificcenter.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String merchantOrderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantOrderStatus status;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne
    private Magazine magazine;
}