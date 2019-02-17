package org.practice.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "ORDER_PARENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OrderParent {
    @Id
    @GeneratedValue
    private int id;
}
