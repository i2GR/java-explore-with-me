package ru.practicum.ewm.stats.model;

import lombok.*;
import ru.practicum.ewm.common.utils.StatsAppName;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private StatsAppName app;

    @Column
    private String uri;

    private String ip;

    @Column(name = "time_stamp")
    private LocalDateTime timestamp;
}
