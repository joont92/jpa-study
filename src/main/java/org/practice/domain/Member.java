package org.practice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by naver on 2018. 11. 10..
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Getter
@Entity
@Table(name="MEMBER")
public class Member extends BaseEntity{
	@Id
	@GeneratedValue
	private Integer id;

	private String name;

	private String city;

	private String street;

	private String zipCode;

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<Order> orderList = new ArrayList<>();

	public void addOrder(Order order){
		if(!this.orderList.contains(order)){
			this.orderList.add(order);
		}

		order.setMember(this);
	}
}
