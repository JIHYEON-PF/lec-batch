package com.example.pass.repository.booking;

import com.example.pass.repository.BaseEntity;
import com.example.pass.repository.pass.PassEntity;
import com.example.pass.repository.user.UserEntity;
import com.example.pass.util.LocalDateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingSeq;

    private Integer passSeq;

    private String userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private boolean usedPass;

    private boolean attended;

    private LocalDateTime startedAd;

    private LocalDateTime endedAt;

    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    private PassEntity passEntity;

    public LocalDateTime getStatisticsAt() {
        return LocalDateTimeUtils.getMidnightDateTime(this.endedAt);
    }
}
