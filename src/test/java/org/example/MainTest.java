package org.example;

import lombok.Cleanup;
import org.example.entity.*;
import org.example.entity.type.Role;
import org.example.util.HibernateUtil;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class MainTest {

    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();
            var user = session.get(User.class, 10L);
            Chat chat = Chat.builder()
                    .name("tyzdev")
                    .build();

            user.addChat(chat);
            session.save(chat);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();

            //   session.get(User.class, 1L);
            var company = session.get(Company.class, 2);
            User user = User.builder()
                    .company(company)
                    .username("test4@gmail.com")
                    .build();

            Profile profile = Profile.builder()
                    .language("RU")
                    .street("Kolasa 18")
                    .build();
            profile.setUser(user);
            session.save(user);
            // session.save(profile);
            session.getTransaction().commit();
        }
    }

    @Test
    void checkOrphanRemoval() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 1);
            company.getUsers().removeIf(user -> user.getId().equals(1L));

            session.getTransaction().commit();

        }
    }

    @Test
    void checkLazyInitialization() {
        Company company = null;
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();

            company = session.get(Company.class, 1);
            session.delete(company);

            session.getTransaction().commit();

        }
        var users = company.getUsers();
        System.out.println(users.size());
    }


    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

        session.beginTransaction();

        Company company = session.get(Company.class, 1);
        session.delete(company);

        session.getTransaction().commit();

    }

    @Test
    void addUserToNewCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

        session.beginTransaction();

        var company = Company.builder()
                .name("Facebook")
                .build();

        var user = User.builder()
                .username("Sveta")
                .build();

        company.addUser(user);

        session.save(company);
        session.getTransaction().commit();

    }

    @Test
    void oneToMany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();

        session.beginTransaction();

        session.get(Company.class, 1);

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("firstname");
        resultSet.getString("lastname");

        Class<User> userClass = User.class;
        Constructor<User> userConstructor = userClass.getConstructor();
        User user = userConstructor.newInstance();

        Field username = userClass.getDeclaredField("username");
        username.setAccessible(true);
        username.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = User.builder().
                username("ivam@gmail.com")
                //.firstname("ivan")
                //.lastname("Ivanov")
                // .birthDay(new BirthDay(LocalDate.of(2000, 1, 19)))
                .role(Role.ADMIN)
                .build();

        String sql = """
                insert
                   into
                       %s
                       (%s)
                   values
                       (%s)
                """;
        String tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        Field[] declaredFields = user.getClass().getDeclaredFields();
        String columnNames = Arrays.stream(declaredFields).
                map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name).
                        orElse(field.getName()))
                .collect(Collectors.joining(", "));

        String values = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, values));

        Connection connection = null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, values));

        for (Field field : declaredFields) {
            field.setAccessible(true);
            preparedStatement.setObject(1, field.get(user));
        }
    }

}