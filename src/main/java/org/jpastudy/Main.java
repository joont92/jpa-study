package org.jpastudy;

import org.jpastudy.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.function.Consumer;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by naver on 2018. 11. 10..
 */
public class Main {
	private static EntityManagerFactory emf;

	public static void main(String args[]) {
		emf = new AutoScanProvider().createEntityManagerFactory("jpastudy");

		run(Main::setUp);
		run(Main::addCategory);

		emf.close();
	}

	private static void addCategory(EntityManager em){
		Category category = Category.builder()
				.name("meat")
				.parent(null)
				.build();

		em.persist(category);

		Category child1 = Category.builder()
				.name("pork")
				.build();
		child1.setParent(category);

		Category child2 = Category.builder()
				.name("beef")
				.build();
		child2.setParent(category);

		em.persist(child1);
		em.persist(child2);

		assertThat(category.getChild().size(), is(2));
	}


	private static void addOrderWithFind(EntityManager em){
		Order order = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(em.find(Member.class, 1))
				.build();
		em.persist(order);
	}

	private static void addOrderWithGetReference(EntityManager em){
		Order order = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(em.getReference(Member.class, 1))
				.build();
		em.persist(order);
	}

	private static Member find(EntityManager em) {
		return em.find(Member.class, 1);
	}

	private static Member add(EntityManager em) {
		Member member = Member.builder()
				.id(1)
				.city("seoul")
				.street("sinsuro")
				.zipCode("100")
				.build();
		em.persist(member);

		return member;
	}

	private static void setUp(EntityManager em){
		// member add
		Member member1 = Member.builder()
				.city("sungnam")
				.street("신수로")
				.zipCode("200")
				.build();
		Member member2 = Member.builder()
				.city("seoul")
				.street("테헤란로")
				.zipCode("100")
				.build();
		em.persist(member1);
		em.persist(member2);

		// item add
		Item item1 = Item.builder()
				.name("청바지")
				.price(50000)
				.stockQuantity(100)
				.build();
		Item item2 = Item.builder()
				.name("와이셔츠")
				.price(20000)
				.stockQuantity(200)
				.build();
		em.persist(item1);
		em.persist(item2);

		// order add
		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member1)
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member2)
				.build();
		em.persist(order1);
		em.persist(order2);

		// orderItem add
		OrderItem orderItem1 = OrderItem.builder()
				.order(order1)
				.item(item1)
				.count(5)
				.build();
		OrderItem orderItem2 = OrderItem.builder()
				.order(order1)
				.item(item2)
				.count(10)
				.build();
		OrderItem orderItem3 = OrderItem.builder() // 대량 구매자
				.order(order2)
				.item(item1)
				.count(100)
				.build();
		em.persist(orderItem1);
		em.persist(orderItem2);
		em.persist(orderItem3);

		System.out.println("::::::::::::::::::::::::::::::SETUP END::::::::::::::::::::::::::::::::");
	}

	private static void run(Consumer<EntityManager> runner) {
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			runner.accept(em);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
	}
}
