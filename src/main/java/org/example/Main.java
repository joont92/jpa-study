package org.example;

import org.example.domain.dtype.Album;
import org.example.domain.dtype.Movie;

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

		run(Main::joinedTableCrud);

		emf.close();
	}

	private static void joinedTableCrud(EntityManager em){
		Album album = new Album();
		album.setName("test");
		album.setAuthor("author");

		em.persist(album);

		Movie movie = new Movie();
		movie.setName("test movie");
		movie.setActor("actor");

		em.persist(movie);

		em.flush();
		em.clear();

		Album foundAlbum = em.find(Album.class, 1L);
		assertNotNull(foundAlbum);
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
