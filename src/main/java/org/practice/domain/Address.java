package org.practice.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address implements Cloneable{
    private String city;

    private String street;

    private String zipcode;

    @Override
    public Address clone() throws CloneNotSupportedException{
        return (Address)super.clone();
    }
}
