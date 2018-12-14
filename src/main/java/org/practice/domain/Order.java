package org.practice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by naver on 2018. 11. 20..
 */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name="ORDER_")
public class Order extends BaseEntity{
	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder.Default
	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItemList = new ArrayList<>();

	@Temporal(TemporalType.TIMESTAMP)
	private Date orderDate;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;

	public void setMember(Member member){
		if(this.member != null){
			this.member.getOrderList().remove(this);
		}

		this.member = member;

		if(member != null && !member.getOrderList().contains(this)){
			member.getOrderList().add(this);
		}
	}

	public void addOrderItem(OrderItem orderItem){
		if(!this.orderItemList.contains(orderItem)){
			this.orderItemList.add(orderItem);
		}

		orderItem.setOrder(this);
	}

	public void setDelivery(Delivery delivery){
		this.delivery = delivery;
//		delivery.setOrder(this);
	}
}
