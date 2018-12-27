package org.example;

import org.example.domain.cascade.Child;
import org.example.domain.cascade.GrandChild;
import org.example.domain.cascade.Parent;
import org.example.domain.dtype.Album;
import org.example.domain.dtype.Bag;
import org.example.domain.dtype.Movie;
import org.example.domain.linktable.A;
import org.example.domain.linktable.B;
import org.practice.domain.Member;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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

//		run(Main::joinedTableCrud);
//		run(Main::getReferneceTest);
//		run(Main::collectionLazyLoadingTest);
//		run(Main::orphanRemovalTest);
		run(Main::joinedTableCrud);

		emf.close();
	}

	private static void orphanRemovalTest(EntityManager em){
		Parent parent = new Parent();

		Child child = new Child();
		parent.addChild(child);

		GrandChild grandChild = new GrandChild();
		child.addGrandChild(grandChild);

		em.persist(parent);

		em.flush();
		em.clear();

		parent = em.find(Parent.class, 1L);
		em.remove(parent);
	}

	private static void cascadeRangeCheck(EntityManager em){
		Parent parent = new Parent();

		Child child = new Child();
		parent.addChild(child);

		GrandChild grandChild = new GrandChild();
		child.addGrandChild(grandChild);

		em.persist(parent);

		em.flush();
		em.clear();

		em.remove(em.find(Parent.class, 1L));
	}

	private static void collectionLazyLoadingTest(EntityManager em){
		A a = A.builder()
				.id("test")
				.name("test name")
				.build();
		em.persist(a);

		em.persist(B.builder().id("b1").a(a).build());
		em.persist(B.builder().id("b2").a(a).build());

		em.flush();
		em.clear();

		A foundA = em.find(A.class, "test");
		foundA.getBList().get(0);
	}

	private static void getReferneceTest(EntityManager em){
		A a = A.builder()
				.id("test")
				.name("test name")
				.build();
		em.persist(a);

		em.flush();
		em.clear();

		A foundA1 = em.getReference(A.class, "test");
		A foundA2 = em.find(A.class, "test");

//		assertSame(foundA1 ,foundA2);
		System.out.println(foundA1.getClass().getName());
		System.out.println(foundA2.getClass().getName());
	}

	private static void joinedTableCrud(EntityManager em){
		Bag bag = new Bag();
		em.persist(bag);

		Album album = new Album();
		album.setName("test");
		album.setAuthor("author");
		album.setBag(bag);

		em.persist(album);

		Movie movie = new Movie();
		movie.setName("test movie");
		movie.setActor("actor");
		movie.setBag(bag);

		em.persist(movie);

		em.flush();
		em.clear();

		Album foundAlbum = em.find(Album.class, 2L);
		assertNotNull(foundAlbum);

		Bag foundBag = em.find(Bag.class, 1L);
		assertNotNull(foundBag.getItemList().get(0));
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
