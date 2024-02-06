package org.example;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {

        User user = User.builder()
                .username("ivan@gmail.com")
                .lastname("ivanov")
                .firstname("ivan")
                .build();

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            try (Session session1 = sessionFactory.openSession()) {
                session1.beginTransaction();

                session1.saveOrUpdate(user);

                session1.getTransaction().commit();
            }
            try (Session session2 = sessionFactory.openSession()) {
                session2.beginTransaction();
                // refresh, merge
//                User freshUser = session2.get(User.class, "ivan@gmail.com");
//                freshUser.setFirstname(user.getFirstname());
//                freshUser.setUsername(user.getUsername());
//                session2.delete(user);

                Object mergedUser = session2.merge(user);
                user.setFirstname("Sveta");
                session2.refresh(user);
                session2.getTransaction().commit();

            }
        }
    }
}