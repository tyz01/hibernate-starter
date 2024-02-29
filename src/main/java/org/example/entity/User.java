package org.example.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.example.entity.type.Role;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"company", "profile", "chats"})
@EqualsAndHashCode(of = "username")
@TypeDef(name = "example", typeClass = JsonBinaryType.class)
//@Access(AccessType.PROPERTY) // default AccessType.FIELD
public class User {
    @Id
    // @GeneratedValue(generator = "user_gen", strategy = GenerationType.TABLE)
    // @TableGenerator(name = "user_gen", sequenceName = "all_sequence",
    // pkColumnName = "table_name", allocationSize = 1, valueColumnName = "pk_value" )
    // @GeneratedValue(generator = "user_gen", strategy = GenerationType.SEQUENCE)
    // @SequenceGenerator(name = "user_gen", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;

    //@EmbeddedId
    @Embedded
    @AttributeOverride(name = "birthDay", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    //@Transient
    @Enumerated(EnumType.STRING)
    private Role role;

    @Type(type = "example")
    private String info;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // cascade = CascadeType.ALL
    @JoinColumn(name = "company_id") // default company_id
    private Company company;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false)
    private Profile profile;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "users_chat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id"))
    private Set<Chat> chats = new HashSet<>();

    public void addChat(Chat chat) {
        chats.add(chat);
        chat.getUsers().add(this);
    }

}
