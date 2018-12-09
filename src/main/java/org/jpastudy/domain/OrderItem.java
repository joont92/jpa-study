package org.jpastudy.domain;

import lombok.*;

import javax.persistence.*;

/**
 * Created by naver on 2018. 11. 20..
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name="ORDER_ITEM")
public class OrderItem {
	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(optional=false)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(optional=false)
	@JoinColumn(name = "item_id")
	private Item item;

	private long orderPrice;

	private long count;

	public void setOrder(Order order){
		this.order = order;

		if(!order.getOrderItemList().contains(this)){
			order.getOrderItemList().add(this);
		}
	}
}
