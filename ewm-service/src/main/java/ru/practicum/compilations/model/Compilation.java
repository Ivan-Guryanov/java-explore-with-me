package ru.practicum.compilations.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.events.model.Event;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    private Boolean pinned;

    private String title;
}