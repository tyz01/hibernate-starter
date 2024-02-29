package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.BirthDay;
import org.example.entity.Company;
import org.example.entity.PersonalInfo;
import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Slf4j
public class Main {
    public static void main(String[] args) {
        Company company = Company.builder()
                .name("Google")
                .build();
        User user = User.builder()
                .username("petr1@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .lastname("petrov")
                        .firstname("petr")
                        .build())
                .company(company)
                .build();

        log.info("user is created: {}", user);
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.getTransaction();
                session1.beginTransaction();

               // session1.save(company);
               // session1.save(user);

                session1.getTransaction().commit();
            }
        } catch (Exception exception){
            log.error("Exception occurred: ", exception);
            throw exception;
        }
    }
}