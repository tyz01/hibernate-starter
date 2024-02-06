package org.example.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.example.entity.type.Role;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TypeDef(name = "example", typeClass = JsonBinaryType.class)
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    //@Convert(converter = BirthDayConvertor.class)
    @Column(name = "birth_date")
    private BirthDay birthDay;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Type(type = "example")
    private String info;

}
