package ru.practicum.events.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.categories.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title; //название

    @Column(nullable = false, length = 2000)
    private String annotation; //Краткое описание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; //    Категория

    @Column(name = "confirmed_requests")
    @Builder.Default
    private Long confirmedRequests = 0L; //Количество одобренных заявок на участие в данном событии

    @Column(name = "created_on")
    private LocalDateTime createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(length = 7000)
    private String description;  //Полное описание события

    @Column(name = "event_date")
    private LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator; //    Пользователь (краткая информация)

    @Embedded
    private Location location; //    Широта и долгота места проведения события

    private boolean paid; //Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Column(name = "published_on")
    private LocalDateTime publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "request_moderation")
    @Builder.Default
    private boolean requestModeration = true; //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private State state = State.PENDING; //Список состояний жизненного цикла события

    private Long views; //Количество просмотрев события

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event item = (Event) o;
        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}