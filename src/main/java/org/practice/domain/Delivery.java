package org.practice.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name="DELIVERY")
public class Delivery {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    private String city;

    private String street;

    private String zipCode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public void setOrder(Order order){
        this.order = order;
//        order.setDelivery(this);
    }
}
