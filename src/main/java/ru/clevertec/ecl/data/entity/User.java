package ru.clevertec.ecl.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;
import ru.clevertec.ecl.data.entity.converter.UserRoleConverter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "users")
@Where(clause = "deleted = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    @Convert(converter = UserRoleConverter.class)
    private UserRole userRole;

    @Column(name = "deleted")
    private boolean deleted;

    public enum UserRole {
        ADMIN, MANAGER, USER
    }

    public UserRole getRole() {
        return userRole;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(obj))
            return false;
        User other = (User) obj;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", password=" + password + ", userRole=" + userRole + "]";
    }


}
