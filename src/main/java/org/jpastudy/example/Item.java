package org.jpastudy.example;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by naver on 2018. 11. 20..
 */
@Getter
@Entity
@Table(name="ITEM")
public class Item {
	@Id
	@GeneratedValue
	private int id;

	private String name;
	private long price;
	private long stockQuantity;

	@OneToMany(mappedBy = "item")
	private List<OrderItem> orderItemList;
}
