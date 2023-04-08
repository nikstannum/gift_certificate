package ru.clevertec.ecl.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;
import ru.clevertec.ecl.data.entity.converter.OrderStatusConverter;

@Getter
@Setter
@Where(clause = "deleted = false")
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "status_id")
    @Convert(converter = OrderStatusConverter.class)
    private Status status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderInfo> details;

    @Transient
    @Column(name = "deleted")
    private boolean deleted;

    public enum Status {
        PENDING, PAID, DELIVERED, CANCELED
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
        Order other = (Order) obj;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", user=" + user.getEmail() + ", totalCost=" + totalCost + ", status=" + status
                + "]";
    }
}
