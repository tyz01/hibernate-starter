package org.example;

import org.example.entity.BirthDay;
import org.example.entity.User;
import org.example.entity.type.Role;
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
import java.util.stream.Collectors;

class MainTest {

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
                .firstname("ivan")
                .lastname("Ivanov")
                .birthDay(new BirthDay(LocalDate.of(2000, 1, 19)))
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

        for (Field field: declaredFields) {
            field.setAccessible(true);
            preparedStatement.setObject(1, field.get(user));
        }
    }

}