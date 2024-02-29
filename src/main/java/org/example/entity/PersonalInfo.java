package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PersonalInfo
       // implements Serializable
{
 //   @Serial
//    private static final long serialVersionUID = 8192438042396468984L;
    private String firstname;
    private String lastname;

    //@Convert(converter = BirthDayConvertor.class)
    //@Column(name = "birth_date")
   // @Temporal(TemporalType.TIMESTAMP)
    //@Formula("UPDATE")
    //@ColumnTransformer
    private BirthDay birthDay;
}
