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
@NamedQuery(
		name = "Member.findByName",
		query = "SELECT m FROM Member m WHERE m.name = :name"
)
public class Member extends BaseEntity implements Memoable{
	@Id
	@GeneratedValue
	private Integer id;

	private String name;

	@Embedded
	private Address homeAddress;

	private boolean test;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "city", column = @Column(name = "company_city")),
			@AttributeOverride(name = "street", column = @Column(name = "company_street")),
			@AttributeOverride(name = "zipcode", column = @Column(name = "company_zipcode"))
	})
	private Address companyAddress;

	@Builder.Default
	@ElementCollection
	@CollectionTable(
			name = "FAVORITE_FOOD",
			joinColumns = @JoinColumn(name = "member_id")
	)
	@Column(name = "food_name")
	private List<String> favoriteFoodList = new ArrayList<>();

	@Builder.Default
	@ElementCollection
	@CollectionTable(
			name = "ADDRESS_HISTORY",
			joinColumns = @JoinColumn(name = "member_id")
	)
	private List<Address> addressHistory = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("orderDate desc")
	private List<Order> orderList = new ArrayList<>();

	public void addOrder(Order order){
		if(this.orderList == null){
			this.orderList = new ArrayList<>();
		}

		if(!this.orderList.contains(order)){
			this.orderList.add(order);
		}

		order.setMember(this);
	}
}
