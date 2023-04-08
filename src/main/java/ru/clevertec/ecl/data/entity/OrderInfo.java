package ru.clevertec.ecl.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "order_infos")
@Where(clause = "deleted = false")
public class OrderInfo {
    @Id
    @Column(name = "order_infos_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "certificate_id")
    private GiftCertificate giftCertificate;

    @Column(name = "certificate_quantity")
    private Integer certificateQuantity;

    @Column(name = "certificate_price")
    private BigDecimal certificatePrice;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "order_id")
    private Order order;

    @Transient
    @Column(name = "deleted")
    private boolean deleted;

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
        OrderInfo other = (OrderInfo) obj;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "OrderInfo [id=" + id + ", orderId=" + order.getId() + ", certificate=" + giftCertificate.getName() + ", certificateQuantity="
                + certificateQuantity + ", certificatePrice=" + certificatePrice + "]";
    }
}
