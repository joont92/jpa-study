package org.practice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by naver on 2018. 11. 20..
 */
//@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
/*
@Table(name="ITEM")
*/
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
	@Id
	@GeneratedValue
	private int id;

	protected String name;

	protected long price;

	public long stockQuantity;

//	@Builder.Default
	@ManyToMany
	@JoinTable(name = "CATEGORY_ITEM",
			joinColumns = @JoinColumn(name = "item_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private List<Category> categoryList = new ArrayList<>();

	public void addCategory(Category category){
		if(!this.categoryList.contains(category)){
			this.categoryList.add(category);
		}

		if(!category.getItemList().contains(this)){
			category.getItemList().add(this);
		}
	}
}
