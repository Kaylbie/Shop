package com.kursinis.prif4kursinis.hibernateControllers;

import com.kursinis.prif4kursinis.model.Cart;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

public class GenericHib {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager em;

    public GenericHib(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    //<T> indikuos, kad yra generic method. Ka tik padariau visu klasiu create backend metoda
    public <T> void create(T entity) {
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    //Ka tik padariau update visoms esybems
    public <T> void update(T entity) {
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    //Su delete bus niuansai, kol kas nerasau
    public <T> void delete(Class<T> entityClass, int id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            var object = em.find(entityClass, id);
            em.remove(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    public List<Cart> findCartsByQuery(String query) {
        EntityManager em = null;
        List<Cart> carts = new ArrayList<>();
        try {
            em = getEntityManager();
            int cartId = Integer.parseInt(query); // Assuming query is a cart ID
            String queryStr = "SELECT c FROM Cart c WHERE c.id = :cartId";
            TypedQuery<Cart> typedQuery = em.createQuery(queryStr, Cart.class);
            typedQuery.setParameter("cartId", cartId);
            carts = typedQuery.getResultList();
        } catch (NumberFormatException e) {
            // Handle invalid cart ID format
            System.out.println("Invalid cart ID format in query.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
        return carts;
    }


    //Get by id visoms esybems READ
    public <T> T getEntityById(Class<T> entityClass, int id) {
        T result = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            result = em.find(entityClass, id);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //READ operacija, tik istrauks visus irasus, kurie yra DB

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        EntityManager em = null;
        List<T> result = new ArrayList<>();
        try {
            em = getEntityManager();
            CriteriaQuery query = em.getCriteriaBuilder().createQuery();
            query.select(query.from(entityClass));
            Query q = em.createQuery(query);
            result = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
        return result;
    }
}
