package org.practice;

import org.practice.domain.*;
import org.practice.domain.item.Album;

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

//		run(Main::setUp);
//		run(Main::addCategory);
//		run(Main::연관관계의주인이아닌쪽에서컨트롤_OneToMany);
//		run(Main::연관관계의주인이아닌쪽에서컨트롤_ManyToMany);
//		run(Main::연관관계주인의접근범위);
//		run(Main::oneToOneLazyLoading);
//		run(Main::연관관계의주인이아닌쪽에서컨트롤_OneToOne);
		run(Main::singleDtype);

		emf.close();
	}

	public static void singleDtype(EntityManager em){
		Album album = Album.builder()
				.artist("album")
				.etc("etc")
				.build();

		em.persist(album);

		em.flush();
		em.clear();

		Album foundAlbum = em.find(Album.class, 1);
		assertThat(foundAlbum.getArtist(), is("album"));
	}

	public static void oneToOneLazyLoading(EntityManager em){
		Order order = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		Delivery delivery = Delivery.builder()
				.city("방콕씨리아캔스탑")
				.street("월스트리트")
				.zipCode("111-1111")
				.build();

		em.persist(delivery);
		order.setDelivery(delivery);

		em.persist(order);

		// 저장하고 영속성 컨텍스트 제거
		em.flush();
		em.clear();

		// delivery 조회하면 order 까지 같이 조회됨
//		Delivery foundDelivery = em.find(Delivery.class, 1);
//		assertThat(foundDelivery.getCity(), is("방콕씨리아캔스탑"));

		// lazy loading start 이후에 delivery 조회됨
		Order foundOrder = em.find(Order.class, 2);
		assertThat(foundOrder.getStatus(), is(OrderStatus.ORDER));

		System.out.println("::::: lazy loading start ::::::");
		assertThat(foundOrder.getDelivery().getCity(), is("방콕씨리아캔스탑"));
	}

	public static void 연관관계주인의접근범위(EntityManager em){
		Member member = Member.builder()
				.city("sungnam")
				.street("신수로")
				.zipCode("200")
				.build();
		em.persist(member);

		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		order1.setMember(member);
		order2.setMember(member);

		em.persist(order1);
		em.persist(order2);

		em.flush();
		em.clear();

		// member를 통해 얻은 order
		Order foundOrder = em.find(Member.class, 1)
				.getOrderList().get(0);
		foundOrder.setMember(null);
		em.remove(foundOrder);
	}

	public static void 연관관계의주인이아닌쪽에서컨트롤_OneToOne(EntityManager em) {
		Delivery delivery = Delivery.builder()
				.city("방콕씨리아캔스탑")
				.street("월스트리트")
				.zipCode("111-1111")
				.build();

		Order order = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		em.persist(delivery);

		order.setDelivery(delivery);

		em.persist(order);

//		delivery.setOrder(order);

	}

	public static void 연관관계의주인이아닌쪽에서컨트롤_OneToMany(EntityManager em){
		Member member = Member.builder()
				.city("sungnam")
				.street("신수로")
				.zipCode("200")
				.build();

		Order order = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		member.addOrder(order);

		em.persist(member);

		assertNull(em.find(Order.class, 1));

		order.setMember(member);
		em.persist(order);

		assertNotNull(em.find(Order.class, 2)); // 왜 2가 된걸까
	}

	public static void 연관관계의주인이아닌쪽에서컨트롤_ManyToMany(EntityManager em){
		/*
		Category category = Category.builder()
				.name("meat")
				.parent(null)
				.build();

		Item item = Item.builder()
				.name("청바지")
				.price(50000)
				.stockQuantity(100)
				.build();

		em.persist(category);
		em.persist(item);


		// item만이 control 할 수 있다
//		category.getItemList().add(item);
		item.getCategoryList().add(category);

		// 편의메서드가 반대편 리스트에 add 까지 해주므로 결국 연관관계의 주인이 컨트롤 하는 꼴이 된다
//		category.addItem(item);
//		item.addCategory(category);

		em.flush();
		em.clear();

		Item foundItem = em.find(Item.class, 2);
//		foundItem.getCategoryList().remove(0);
		assertThat(foundItem.getCategoryList().size(), is(1));
		*/
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
		/*
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
		*/
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