package org.practice;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

import org.practice.domain.*;

import javax.persistence.*;
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
//		run(Main::singleDtype);
//		run(Main::checkLazyInitializeTime);
//		run(Main::연관관계_설정시_불필요한sql호출체크);
//		run(Main::cascadeRelationSet);
//		run(Main::changeIdentifier);
//		run(Main::changeIdentifierWithChilds);
//		run(Main::cascadeTest);
//		run(Main::addTest);
//		run(Main::cascadeTest);
//		run(Main::deleteTest);
//		run(Main::collectionFetchJoinTest);
//		run(Main::cascadeTest);
//		run(Main::nPlus1Test);
//		nonTransacation();
//		run(Main::queryDslTest);
//		run(Main::readOnly);
//		rollbackCheck();
		run(Main::anyAssociationTest);

		emf.close();
	}

	private static void anyAssociationTest(EntityManager em){
		Member member = Member.builder()
				.name("ziont")
				.homeAddress(new Address("europe", "street1", "zipCode1"))
				.build();

		Item item = Item.builder()
				.name("청바지")
				.price(100000)
				.stockQuantity(100)
				.build();
		em.persist(member);
		em.persist(item);

		Memo memo1 = new Memo();
		memo1.setResource(member);
		memo1.setContent("this is member memo");
		Memo memo2 = new Memo();
		memo2.setResource(item);
		memo2.setContent("this is item memo");

//		Memo memo1 = Memo.builder()
//				.resource(member)
//				.content("this is member memo")
//				.build();
//		Memo memo2 = Memo.builder()
//				.resource(item)
//				.content("this is member memo")
//				.build();

		em.persist(memo1);
		em.persist(memo2);

		apply(em);

		Memo givenMemo1 = em.find(Memo.class, 3L);
		Memo givenMemo2 = em.find(Memo.class, 4L);
//		assertThat(givenMemo1.getResource().getId());
		System.out.println("this command for debug");
	}

	private static void rollbackCheck(){
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			Member member = em.find(Member.class, 1);
			member.setName("DDDD");

			Member willFailMember = em.getReference(Member.class, 2222);
			willFailMember.getName();

//			Member willFailMember = em.createQuery("select m from Member m where m.id = 9999", Member.class)
//					.getSingleResult();
//			willFailMember.getName();

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
//			tx.rollback();
			tx.commit();
		} finally {
			em.close();
		}
	}

	private static void readOnly(EntityManager em){
		addEntities(em);
		apply(em);

		Member member1 = em.createQuery("select m from Member m where m.id = :id", Member.class)
				.setParameter("id", 1)
				.setHint("org.hibernate.readOnly", true)
				.getSingleResult();
		Member member2 = em.createQuery("select m from Member m where m.id = :id", Member.class)
				.setParameter("id", 1)
				.setHint("org.hibernate.readOnly", true)
				.getSingleResult();

		System.out.println(member1);
		System.out.println(member2);
	}

	private static void noResultException(EntityManager em){
		addEntities(em);
		apply(em);

		Member member = em.find(Member.class, 1);

		try{
//			Member emptyMember =
//					em.createQuery("select m from Member m where m.name = 'test'", Member.class)
//					.getSingleResult();
			Member emptyMember = em.find(Member.class, 1000);
		}catch (EntityNotFoundException e){

		}


		member.setName("test");
	}

	private static void cascadeRemoveTest(EntityManager em){
		Member member = Member.builder().build();
		Order order1 = Order.builder().build();
		Order order2 = Order.builder().build();

		member.addOrder(order1);
		member.addOrder(order2);

		em.merge(member);

		Member givenMember = Member.builder().id(1).build();
		Order givenOrder = Order.builder().build();
		givenOrder.setId(2);
		givenMember.addOrder(givenOrder);

		Member testMember = em.merge(givenMember);
		testMember.getOrderList().get(0);
		System.out.println(":::");
	}


	private static void converter(EntityManager em){
		Member member = Member.builder().test(true).build();
		em.persist(member);
	}

	private static void orderColumn(EntityManager em){
		addEntities(em);
		apply(em);

		Member member = em.find(Member.class, 1);
		member.getOrderList().get(0);
	}

	private static void nosession(EntityManager em){
		addEntities(em);
		apply(em);

		Member member = em.find(Member.class, 1);
		apply(em);

		member.getOrderList().get(0);
	}

	private static void setLazyLoading(EntityManager em){
		addEntities(em);
		apply(em);

		Member member = em.find(Member.class, 1);

		Order order = Order.builder().build();
		member.getOrderList().add(order);
//		member.getOrderList().contains(order);
//		member.getOrderList().remove(order);
	}


	private static void nonTransacation(){
		run(Main::addEntities);

		EntityManager em = emf.createEntityManager();
		em.close();
		Member member = em.find(Member.class, 2);
		em.clear();
//		member.getOrderList().get(0);
	}

	private static void nPlus1Test(EntityManager em){
		addEntities(em);
		apply(em);

		TypedQuery<Order> query = em.createQuery("select o from Order o inner join o.member", Order.class);
		List<Order> list = query.getResultList();
		System.out.println(list.size());
	}

	private static void jqplFlushTest(EntityManager em){
		addEntities(em);
		apply(em);

		Item item = em.find(Item.class, 7);
		item.setStockQuantity(10);

		Member member1 = em.find(Member.class, 1);
		member1.setName("changed");

//		Order order = em.createQuery("SELECT o FROM Order o WHERE o.id = 5", Order.class).getSingleResult();
		Member member2 = em.createQuery("SELECT o FROM Member o WHERE o.id = 2", Member.class).getSingleResult();
	}

	private static void nativeQueryTest(EntityManager em) {
		addEntities(em);
		apply(em);

		em.createNativeQuery("SELECT id FROM Item WHERE id=7")
				.getSingleResult();
	}

	private static void fuckingMergeTest(EntityManager em){
		addEntities(em);
		apply(em);

		Member member1 = em.find(Member.class, 1);
		member1.getOrderList().get(0);
		Member member2 = Member.builder()
				.id(1)
				.name("ziont")
//				.city("europe")
//				.street("street1")
//				.zipCode("zipcode1")
				.build();

		em.merge(member2);

		System.out.println(":::::::::");
	}

	private static void proxyObjectTest(EntityManager em){
		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

//		System.out.println("before::::::::::" + order1.getMember().getClass());

		em.persist(order1);
		apply(em);

		order1 = em.find(Order.class, 1);
		System.out.println("after::::::::::" + order1.getMember().getClass());
//		System.out.println(member1.getOrderList().get(0));
	}

	private static void queryDslTest(EntityManager em){
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QItem item = QItem.item;
		QMember member = QMember.member;
		QMember memberForSubquery = new QMember("memberForSubquery");
		QOrder order = QOrder.order;

		addEntities(em);
		apply(em);

		/*
		List<Tuple> foundMembers = queryFactory.select(member.name, member.homeAddress.city)
				.from(member)
				.where(member.name.in(
						JPAExpressions.select(memberForSubquery.name)
						.from(memberForSubquery)
				))
				.fetch();

		System.out.println(foundMembers.get(0).getClass());
		System.out.println(foundMembers.get(1).getClass());
		*/

		List<Member> foundMembers = queryFactory.selectFrom(member)
				.where((member.name.eq("jonathan").or(member.homeAddress.city.eq("seoul"))).and(member.name.eq("mesh").or(member.homeAddress.city.eq("seoul"))))
				.fetch();
		for (Member foundMember : foundMembers) {
			System.out.println(foundMember.getName()+"::::::::::::::::");
		}
	}

	private static void addEntities(EntityManager em){
		Member member1 = Member.builder()
				.name("ziont")
				.homeAddress(new Address("europe", "street1", "zipCode1"))
				.build();
		Member member2 = Member.builder()
				.name("joont")
				.homeAddress(new Address("america", "street2", "zipCode2"))
				.build();
		Member member3 = Member.builder()
				.name("jonathan")
				.homeAddress(new Address("seoul", "street3", "zipCode3"))
				.build();
		Member member4 = Member.builder()
				.name("mesh")
				.homeAddress(new Address("seoul", "street4", "zipCode14"))
				.build();
		em.persist(member1);em.persist(member2);em.persist(member3);em.persist(member4);

		Order order1 = Order.builder()
				.member(member1)
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
		Order order2 = Order.builder()
				.member(member4)
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
		em.persist(order1);em.persist(order2);

		Item item1 = Item.builder()
				.name("청바지")
				.price(100000)
				.stockQuantity(100)
				.build();
		Item item2 = Item.builder()
				.name("흰티")
				.price(30000)
				.stockQuantity(300)
				.build();
		Item item3 = Item.builder()
				.name("코트")
				.price(500000)
				.stockQuantity(10)
				.build();
		em.persist(item1);em.persist(item2);em.persist(item3);
	}

	private static void cascadeTest2(EntityManager em){
		Member member = Member.builder()
				.name("joont")
				.homeAddress(new Address("seoul", "street", "zipCode1"))
				.build();
		member = em.merge(member);

		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		member.addOrder(order1);
		member.addOrder(order2);

		em.flush();
		em.clear();

		member = em.find(Member.class, 1);

		member.getOrderList().clear();

		Order order3 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
//		Order order4 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
		member.addOrder(order3);
	}

	private static void addTest2(EntityManager em){
		Member member = Member.builder()
				.name("joont")
				.homeAddress(new Address("seoul", "street", "zipCode1"))
				.build();
		em.persist(member);

		Member member1 = em.find(Member.class, 1);
		member1.setName("modified name");

		Member member2 = em.createQuery("select m from Member m where id = 1", Member.class)
				.getSingleResult();

		assertSame(member1, member2);

//		Object order = em.createQuery("select o from Order o where id = 5")
//				.getSingleResult();
	}

	private static void possibleProblemOnBulk(EntityManager em){
		memberAdd(em);
		apply(em);

		Member member = em.find(Member.class, 1);
		assertThat(member.getName(), is("joont"));

		String sql = "update Member m " +
				"set m.name = 'joonta'";
		em.createQuery(sql).executeUpdate();

		em.refresh(member);

		assertThat(member.getName(), is("joonta"));
	}

	private static void bulkTest(EntityManager em){
		memberAdd(em);
		apply(em);

		String sql = "update Member m " +
				"set m.name = 'seoul-joont' " +
				"where m.homeAddress.city = :city";
		int resultCount = em.createQuery(sql)
				.setParameter("city", "seoul")
				.executeUpdate();

		assertThat(resultCount, is(2));

		sql = "delete from Member m " +
				"where m.homeAddress.zipcode like :zipCode";
		resultCount = em.createQuery(sql)
				.setParameter("zipCode", "zipCode%")
				.executeUpdate();

		assertThat(resultCount, is(4));
	}

	private static void namedQuery(EntityManager em){
		memberAdd(em);
		apply(em);

		List<Member> result = em.createNamedQuery("Member.findByName", Member.class)
				.setParameter("name", "joont")
				.getResultList();
	}

	private static void memberAdd(EntityManager em){
		Member member1 = Member.builder()
				.name("joont")
				.homeAddress(new Address("seoul", "street", "zipCode1"))
				.build();
		Member member2 = Member.builder()
				.name("joont")
				.homeAddress(new Address("seoul", "no-street", "zipCode2"))
				.build();
		Member member3 = Member.builder()
				.name("joont")
				.homeAddress(new Address("busan", "no-street", "zipCode3"))
				.build();
		Member member4 = Member.builder()
				.name("joont")
				.homeAddress(new Address("busan", "street", "zipCode4"))
				.build();
		em.persist(member1);em.persist(member2);em.persist(member3);em.persist(member4);

		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member1)
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member4)
				.build();
		em.persist(order1);
		em.persist(order2);
	}

	private static void cloneTest(EntityManager em){
		Address address = new Address("city", "street", "zipCode");

		try{
			Member member1 = Member.builder()
					.name("joont")
					.homeAddress(address.clone())
					.build();
			Member member2 = Member.builder()
					.name("joont")
					.homeAddress(address.clone())
					.build();

			address.setCity("fucking changed");

			em.persist(member1);
			em.persist(member2);
		} catch (CloneNotSupportedException e){

		}
	}

	private static void listTest(EntityManager em){
		Member member = Member.builder()
				.name("joont")
				.homeAddress(new Address("city", "street", "zipCode1"))
				.build();

		em.persist(member);

		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member)
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.member(member)
				.build();
		em.persist(order1);
		em.persist(order2);

		apply(em);

		Member foundMember = em.find(Member.class, 1);
		System.out.println(foundMember.getOrderList().getClass());
	}

	private static void jpqlTest(EntityManager em){
		Delivery delivery = Delivery.builder()
				.address(new Address("seoul", "새마을로", "1111-1111"))
				.deliveryStatus(DeliveryStatus.COMP)
				.build();

		em.persist(delivery);

		apply(em);

		Delivery foundDelivery =
				em.createQuery("select d from Delivery d where d.deliveryStatus like :status", Delivery.class)
						.setParameter("status", DeliveryStatus.COMP)
				.getSingleResult();
//		Delivery foundDelivery = em.createQuery("select d from Delivery d where d.address = :address", Delivery.class)
//				.setParameter("address", new Address("seoul", "새마을로", "1111-1111"))
//				.getSingleResult();
//		Delivery d = Delivery.builder().id(1).build();
//		Delivery foundDelivery = em.createQuery("select d from Delivery d where d = :delivery", Delivery.class)
//				.setParameter("delivery", d)
//				.getSingleResult();

	}

	private static void collectionFetchJoinTest(EntityManager em) {
		Member member = Member.builder()
				.name("joont")
				.homeAddress(new Address("city", "street", "zipCode1"))
				.build();

		Order order1 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();
		Order order2 = Order.builder()
				.status(OrderStatus.ORDER)
				.orderDate(new Date())
				.build();

		member.addOrder(order1);
		member.addOrder(order2);
	}

//	public static void mergeActionTest(EntityManager em){
//		Member member = Member.builder()
////				.id(2)
//				.name("joont")
//				.homeAddress(new Address("city", "street", "zipCode1"))
//				.build();
//		em.merge(member);
//	}
//
//	public static void changeIdentifier(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.homeAddress(new Address("city", "street", "zipCode1"))
//				.build();
//		em.persist(member);
//
//		Member member_ = Member.builder()
//				.name("joont2")
//				.homeAddress(new Address("city", "street", "zipCode1"))
//				.build();
//
//		Order order1_ = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		Order order2_ = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		member_.addOrder(order1_);
//		member_.addOrder(order2_);
//
//		em.persist(member_);
//
//		em.flush();
//		em.clear();
//
//		/*
//		TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m INNER JOIN FETCH m.orderList", Member.class);
//		query.setFirstResult(1);
//		query.setMaxResults(3);
//		List<Member> list = query.getResultList();
//		System.out.println("::::::::::::::" + list.size());
//		*/
//
//		/*
//		TypedQuery<Order> query =
//				em.createQuery("select o from Order o where exists (select m from o.member m where m.name = 'joont')", Order.class);
//
//		query.getResultList();
//		*/
//
//		TypedQuery<String> query = em.createQuery("select TRIM(LEADING 'j' FROM m.name) from Member m", String.class);
//		List<String> list = query.getResultList();
//		for (String s : list) {
//			System.out.println("::::::::" + s);
//		}
//	}
//
//	private static void jpqlQueryTest(EntityManager em){
//		em.persist(Member.builder()
//				.name("joont1")
//				.homeAddress(new Address("city", "street", "zipCode1"))
//				.build());
//		em.persist(Member.builder()
//				.name("joont2")
//				.homeAddress(new Address("city", "street", "zipCode2"))
//				.build());
//		em.persist(Member.builder()
//				.name("joont3")
//				.homeAddress(new Address("citadel", "street", "zipCode3"))
//				.build());
//		Order order1 = Order.builder()
//				.member(em.getReference(Member.class, 1))
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		em.persist(order1);
//		Order order2 = Order.builder()
//				.member(em.getReference(Member.class, 3))
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		em.persist(order2);
//
//		em.flush();
//		em.clear();
//
//		/*
//		TypedQuery<Member> query = em.createQuery("select m from Member m where m.name = 'joont1'", Member.class);
//		List<Member> memberList = query.getResultList();
//		assertThat(memberList.size(), is(1));
//		*/
//
//		/*
//		TypedQuery<Long> query = em.createQuery("select count(distinct m.homeAddress.city) from Member m", Long.class);
//		assertThat(query.getSingleResult(), is(1L));
//		*/
//
////		TypedQuery<Long> query =
////				em.createQuery("select count(o) from Order o group by o.member.homeAddress.city", Long.class);
//
//		TypedQuery<Long> query =
//				em.createQuery("select count(o) from Order o LEFT JOIN o.member m " +
//						"group by m.homeAddress.city", Long.class);
//		query.getResultList();
//	}
//
//	private static void deleteTest(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.homeAddress(new Address("city", "street", "zipCode1"))
//				.build();
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		member.addOrder(order1);
//		member.addOrder(order2);
//
//		em.persist(member);
//
//		em.flush();
//		em.clear();
//
//		Member foundMember = em.find(Member.class, 1);
//		Order order3 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		foundMember.addOrder(order3);
////		List<Order> orderList = foundMember.getOrderList();
////		for (Order order : orderList) {
////			em.remove(order);
////		}
////		orderList.clear();
//	}
//
//	private static void elementCollectionTest(EntityManager em){
//		// add
//		Member member = Member.builder()
//				.name("joont1")
//				.build();
//
//		member.setHomeAddress(new Address("city", "street", "zipCode4"));
//		member.getFavoriteFoodList().add("pork");
//		member.getFavoriteFoodList().add("beef");
//
//		member.getAddressHistory().add(new Address("city1", "street1", "zipcode1"));
//		member.getAddressHistory().add(new Address("city2", "street2", "zipcode2"));
//		member.getAddressHistory().add(new Address("city3", "street3", "zipcode3"));
//
//		em.persist(member);
//
//		// clear
//		em.flush();
//		em.clear();
//
//		// modify
//		 member = em.find(Member.class, 1);
//		List<String> favoriteFoodList = member.getFavoriteFoodList();
//		favoriteFoodList.set(0, "changed pork");
//		favoriteFoodList.set(1, "changed beef");
//
//		List<Address> addressHisotry = member.getAddressHistory();
//		addressHisotry.get(0).setStreet("changed street");
//	}
//
//	private static void shareEmbedded(EntityManager em){
//		Address address1 = new Address("city", "street", "zipCode1");
//		Address address2 = address1;
//		address2.setZipcode("zipcode2");
//
//		Member member1 = Member.builder()
//				.name("joont1")
//				.homeAddress(address1)
//				.build();
//		Member member2 = Member.builder()
//				.name("joont2")
//				.homeAddress(address2)
//				.build();
//		em.persist(member1);
//		em.persist(member2);
//
//		em.flush();
//		em.clear();
//
//
//	}
//
//	private static void 준영속Test(EntityManager em){
//		Member member = Member.builder()
//				.id(12)
//				.build();
//
//		em.merge(member);
//	}
//
//	public static void manyToManyCascadeTest(EntityManager em){
////		Item item = Item.builder()
////				.name("item1")
//	}
//
//	public static void cascadeTest2(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		member.addOrder(order1);
//		member.addOrder(order2);
//
//		em.persist(member);
//
//		apply(em);
//
////		Member givenMember = em.merge(member);
////		assertNotSame(member, givenMember);
////		assertTrue(em.contains(givenMember));
//		Member foundMember = em.find(Member.class, 1);
//
//		Order order3 = Order.builder()
////				.id(2)
//				.status(OrderStatus.CANCEL)
//				.orderDate(new Date())
//				.build();
//
//		foundMember.addOrder(order3);
//
////		member.setName("test");
//	}
//
//	private static void addTest(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		em.persist(member);
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order1.setMember(member);
//		em.persist(order1);
//
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order2.setMember(member);
//		em.persist(order2);
//
//		em.flush();
//		em.clear();
//
//		Member newMember = Member.builder()
//				.id(1)
//				.name("joont")
//				.city("city2")
//				.street("street2")
//				.zipCode("zipcode2")
//				.build();
//		Member foundMember = em.find(Member.class, 1);
//
//		newMember.getOrderList().addAll(foundMember.getOrderList());
//		em.merge(newMember);
//
//
//	}
//
//	private static void removeTest(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		em.persist(member);
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order1.setMember(member);
//		em.persist(order1);
//
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order2.setMember(member);
//		em.persist(order2);
//
//		em.flush();
//		em.clear();
//
//		Member foundMember = em.find(Member.class, 1);
//		List<Order> list = foundMember.getOrderList();
//
//
//		for (Order order : list) {
//			em.remove(order);
//		}
//
//		foundMember.getOrderList().clear();
//	}
//
//	private static void changeIdentifierWithChilds(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		em.persist(member);
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order1.setMember(member);
//		em.persist(order1);
//
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		order2.setMember(member);
//		em.persist(order2);
//
//		em.flush();
//		em.clear();
//
//		Member newMember = Member.builder()
//				.name("joont")
//				.city("city2")
//				.street("street2")
//				.zipCode("zipcode2")
//				.build();
//
//		Member foundMember = em.find(Member.class, 1);
//		newMember.setId(foundMember.getId());
//
////		foundMember.getOrderList().clear();
//
//
////		foundMember.setCity("changed city"); // ignore
//		// 영향받지 않았음
////		Order foundOrder = em.find(Order.class, 2);
////		assertThat(foundOrder.getMember().getId(), is(1));
////
//		// new order add
//		Order order3 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
////		newMember.setOrderList(foundMember.getOrderList());
//		newMember.addOrder(order3);
//
//		em.merge(newMember); // merge의 경우 준영속 객체를 영속 객체로 만드는데, id가 있으니까 준영속 객체로 볼 수 있다
////
////		em.flush();
////		em.clear();
//
////		assertThat(newMember.getOrderList().size(), is(2));
//
////		foundMember = em.find(Member.class, 1);
////		assertNotNull(foundMember.getOrderList().get(0));
//	}
//
//	private static void cascadeTest(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		member = em.merge(member);
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		member.addOrder(order1);
//		member.addOrder(order2);
//
//		em.flush();
//		em.clear();
//
//		member = em.find(Member.class, 1);
//
//		member.getOrderList().clear();
//
//		Order order3 = Order.builder()
//				.id(10)
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
////		Order order4 = Order.builder()
////				.status(OrderStatus.ORDER)
////				.orderDate(new Date())
////				.build();
//		member.addOrder(order3);
////		assertThat(member.getOrderList().size(), is(1));
//
//		em.flush();
//		em.clear();
//
//		member = em.find(Member.class, 1);
//		assertThat(member.getOrderList().size(), is(3));
////		member.addOrder(order4);
//	}
//
//	public static void mergeActionTest(EntityManager em){
//		Member member = Member.builder()
////				.id(2)
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		em.merge(member);
//	}
//
//	public static void changeIdentifier(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("city1")
//				.street("street1")
//				.zipCode("zipcode1")
//				.build();
//		em.persist(member);
//
//		em.flush();
//		em.clear();
//
//		Member newMember = Member.builder()
//				.name("joont")
//				.city("city2")
//				.street("street2")
//				.zipCode("zipcode2")
//				.build();
//
//		Member foundMember = em.find(Member.class, 1);
//		newMember.setId(foundMember.getId());
//
//		em.merge(newMember); // merge의 경우 준영속 객체를 영속 객체로 만드는데, id가 있으니까 준영속 객체로 볼 수 있다
//
//		foundMember.setCity("changed city"); // ignore
//	}
//
//	public static void cascadeRelationSet(EntityManager em){
//		Item item = Item.builder()
//				.name("청바지")
//				.price(50000)
//				.stockQuantity(100)
//				.build();
//		em.persist(item);
//
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		OrderItem orderItem1 = OrderItem.builder()
//				.orderPrice(1000)
//				.item(item)
//				.build();
//		OrderItem orderItem2 = OrderItem.builder()
//				.orderPrice(2000)
//				.item(item)
//				.build();
//
////		order.getOrderItemList().add(orderItem1);
////		order.getOrderItemList().add(orderItem2);
//		order.addOrderItem(orderItem1);
//		order.addOrderItem(orderItem2);
//
//		em.persist(order);
//
//		em.flush();
//		em.clear();
//
//		Order foundOrder = em.find(Order.class, 2);
////		assertThat(foundOrder.getOrderItemList().size(), is(2));
//
//		// 변경 감지 체크
////		foundOrder.getOrderItemList().remove(0);
//
//		OrderItem orderItem3 = OrderItem.builder()
//				.orderPrice(3000)
//				.item(item)
//				.build();
////		em.persist(orderItem3);
//
////		foundOrder.getOrderItemList().add(orderItem3);
//		foundOrder.addOrderItem(orderItem3);
////		em.persist(foundOrder);
//	}
//
//	public static void 연관관계_설정시_불필요한sql호출체크(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("sungnam")
//				.street("신수로")
//				.zipCode("200")
//				.build();
//		em.persist(member);
//
//		em.flush();
//		em.clear();
//
//		Order order = Order.builder()
//				.member(em.getReference(Member.class, 1))
//				.build();
//
//		em.persist(order);
//
//
//	}
//
//	public static void checkLazyInitializeTime(EntityManager em){
//		Member member = Member.builder()
//				.name("joont")
//				.city("sungnam")
//				.street("신수로")
//				.zipCode("200")
//				.build();
//
//		em.persist(member);
//
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		order.setMember(member);
//		em.persist(order);
//
//		em.flush();
//		em.clear();
//
//		Order foundOrder = em.find(Order.class, 2);
//		assertNotNull(foundOrder.getMember());
//		assertThat(foundOrder.getMember().getId(), is(1));
//	}
//
//	public static void singleDtype(EntityManager em){
//		/*
//		Album album = Album.builder()
//				.artist("album")
//				.etc("etc")
//				.build();
//
//		em.persist(album);
//
//		em.flush();
//		em.clear();
//
//		Album foundAlbum = em.find(Album.class, 1);
//		assertThat(foundAlbum.getArtist(), is("album"));
//		*/
//	}
//
//	public static void oneToOneLazyLoading(EntityManager em){
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		Delivery delivery = Delivery.builder()
//				.city("방콕씨리아캔스탑")
//				.street("월스트리트")
//				.zipCode("111-1111")
//				.build();
//
//		em.persist(delivery);
//		order.setDelivery(delivery);
//
//		em.persist(order);
//
//		// 저장하고 영속성 컨텍스트 제거
//		em.flush();
//		em.clear();
//
//		// delivery 조회하면 order 까지 같이 조회됨
////		Delivery foundDelivery = em.find(Delivery.class, 1);
////		assertThat(foundDelivery.getCity(), is("방콕씨리아캔스탑"));
//
//		// lazy loading start 이후에 delivery 조회됨
//		Order foundOrder = em.find(Order.class, 2);
//		assertThat(foundOrder.getStatus(), is(OrderStatus.ORDER));
//
//		System.out.println("::::: lazy loading start ::::::");
//		assertThat(foundOrder.getDelivery().getCity(), is("방콕씨리아캔스탑"));
//	}
//
//	public static void 연관관계주인의접근범위(EntityManager em){
//		Member member = Member.builder()
//				.city("sungnam")
//				.street("신수로")
//				.zipCode("200")
//				.build();
//		em.persist(member);
//
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		order1.setMember(member);
//		order2.setMember(member);
//
//		em.persist(order1);
//		em.persist(order2);
//
//		em.flush();
//		em.clear();
//
//		// member를 통해 얻은 order
//		Order foundOrder = em.find(Member.class, 1)
//				.getOrderList().get(0);
//		foundOrder.setMember(null);
//		em.remove(foundOrder);
//	}
//
//	public static void 연관관계의주인이아닌쪽에서컨트롤_OneToOne(EntityManager em) {
//		Delivery delivery = Delivery.builder()
//				.city("방콕씨리아캔스탑")
//				.street("월스트리트")
//				.zipCode("111-1111")
//				.build();
//
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		em.persist(delivery);
//
//		order.setDelivery(delivery);
//
//		em.persist(order);
//
////		delivery.setOrder(order);
//
//	}
//
//	public static void 연관관계의주인이아닌쪽에서컨트롤_OneToMany(EntityManager em){
//		Member member = Member.builder()
//				.city("sungnam")
//				.street("신수로")
//				.zipCode("200")
//				.build();
//
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.build();
//
//		member.addOrder(order);
//
//		em.persist(member);
//
//		assertNull(em.find(Order.class, 1));
//
//		order.setMember(member);
//		em.persist(order);
//
//		assertNotNull(em.find(Order.class, 2)); // 왜 2가 된걸까
//	}
//
//	public static void 연관관계의주인이아닌쪽에서컨트롤_ManyToMany(EntityManager em){
//		/*
//		Category category = Category.builder()
//				.name("meat")
//				.parent(null)
//				.build();
//
//		Item item = Item.builder()
//				.name("청바지")
//				.price(50000)
//				.stockQuantity(100)
//				.build();
//
//		em.persist(category);
//		em.persist(item);
//
//
//		// item만이 control 할 수 있다
////		category.getItemList().add(item);
//		item.getCategoryList().add(category);
//
//		// 편의메서드가 반대편 리스트에 add 까지 해주므로 결국 연관관계의 주인이 컨트롤 하는 꼴이 된다
////		category.addItem(item);
////		item.addCategory(category);
//
//		em.flush();
//		em.clear();
//
//		Item foundItem = em.find(Item.class, 2);
////		foundItem.getCategoryList().remove(0);
//		assertThat(foundItem.getCategoryList().size(), is(1));
//		*/
//	}
//
//	private static void addCategory(EntityManager em){
//		Category category = Category.builder()
//				.name("meat")
//				.parent(null)
//				.build();
//
//		em.persist(category);
//
//		Category child1 = Category.builder()
//				.name("pork")
//				.build();
//		child1.setParent(category);
//
//		Category child2 = Category.builder()
//				.name("beef")
//				.build();
//		child2.setParent(category);
//
//		em.persist(child1);
//		em.persist(child2);
//
//		assertThat(category.getChild().size(), is(2));
//	}
//
//
//	private static void addOrderWithFind(EntityManager em){
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.member(em.find(Member.class, 1))
//				.build();
//		em.persist(order);
//	}
//
//	private static void addOrderWithGetReference(EntityManager em){
//		Order order = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.member(em.getReference(Member.class, 1))
//				.build();
//		em.persist(order);
//	}
//
//	private static Member find(EntityManager em) {
//		return em.find(Member.class, 1);
//	}
//
//	private static Member add(EntityManager em) {
//		Member member = Member.builder()
//				.id(1)
//				.city("seoul")
//				.street("sinsuro")
//				.zipCode("100")
//				.build();
//		em.persist(member);
//
//		return member;
//	}
//
//	private static void setUp(EntityManager em){
//		/*
//		// member add
//		Member member1 = Member.builder()
//				.city("sungnam")
//				.street("신수로")
//				.zipCode("200")
//				.build();
//		Member member2 = Member.builder()
//				.city("seoul")
//				.street("테헤란로")
//				.zipCode("100")
//				.build();
//		em.persist(member1);
//		em.persist(member2);
//
//		// item add
//		Item item1 = Item.builder()
//				.name("청바지")
//				.price(50000)
//				.stockQuantity(100)
//				.build();
//		Item item2 = Item.builder()
//				.name("와이셔츠")
//				.price(20000)
//				.stockQuantity(200)
//				.build();
//		em.persist(item1);
//		em.persist(item2);
//
//		// order add
//		Order order1 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.member(member1)
//				.build();
//		Order order2 = Order.builder()
//				.status(OrderStatus.ORDER)
//				.orderDate(new Date())
//				.member(member2)
//				.build();
//		em.persist(order1);
//		em.persist(order2);
//
//		// orderItem add
//		OrderItem orderItem1 = OrderItem.builder()
//				.order(order1)
//				.item(item1)
//				.count(5)
//				.build();
//		OrderItem orderItem2 = OrderItem.builder()
//				.order(order1)
//				.item(item2)
//				.count(10)
//				.build();
//		OrderItem orderItem3 = OrderItem.builder() // 대량 구매자
//				.order(order2)
//				.item(item1)
//				.count(100)
//				.build();
//		em.persist(orderItem1);
//		em.persist(orderItem2);
//		em.persist(orderItem3);
//
//		System.out.println("::::::::::::::::::::::::::::::SETUP END::::::::::::::::::::::::::::::::");
//		*/
//	}

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

	private static void apply(EntityManager em){
		em.flush();
		em.clear();
	}
}
