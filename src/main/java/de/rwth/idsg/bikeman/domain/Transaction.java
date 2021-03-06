package de.rwth.idsg.bikeman.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "T_TRANSACTION",
        indexes = {
                @Index(columnList="pedelec_id", unique = false),
                @Index(columnList="from_slot_id", unique = false),
                @Index(columnList="to_slot_id", unique = false) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@TableGenerator(name="transaction_gen", initialValue=0, allocationSize=1)
@EqualsAndHashCode(of = {"transactionId"})
@ToString(includeFieldNames = true, exclude = {"cardAccount"})
@Getter
@Setter
public class Transaction implements Serializable {
    private static final long serialVersionUID = 7444900986847008432L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transaction_gen")
    @Column(name = "transaction_id")
    private long transactionId;

    @Column(name = "start_datetime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime endDateTime;

    @ManyToOne
    @JoinColumn(name = "pedelec_id")
    private Pedelec pedelec;

    @ManyToOne
    @JoinColumn(name = "from_slot_id")
    private StationSlot fromSlot;

    @ManyToOne
    @JoinColumn(name = "to_slot_id")
    private StationSlot toSlot;

    @ManyToOne
    @JoinColumn(name = "card_account_id")
    private CardAccount cardAccount;

    @ManyToOne
    @JoinColumn(name = "booked_tariff_id")
    private BookedTariff bookedTariff;

    @Column(name = "fees")
    private BigDecimal fees;

}
