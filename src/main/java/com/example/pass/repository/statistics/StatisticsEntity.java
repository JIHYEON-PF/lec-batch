package com.example.pass.repository.statistics;

import com.example.pass.repository.booking.BookingEntity;
import com.example.pass.repository.booking.BookingStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statisticsSeq;

    private LocalDateTime statisticsAt;

    private int allCount;

    private int attendedCount;

    private int cancelledCount;

    public static StatisticsEntity create(final BookingEntity booking) {
        StatisticsEntity statisticsEntity = new StatisticsEntity();

        statisticsEntity.setStatisticsAt(booking.getStatisticsAt());
        statisticsEntity.setAllCount(1);
        if (booking.isAttended()) {
            statisticsEntity.setStatisticsSeq(1);
        }
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            statisticsEntity.setCancelledCount(1 );
        }

        return statisticsEntity;
    }

    public void add(final BookingEntity booking) {
        this.allCount++;

        if (booking.isAttended()) {
            this.attendedCount++;
        }

        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            this.cancelledCount++;
        }
    }
}
