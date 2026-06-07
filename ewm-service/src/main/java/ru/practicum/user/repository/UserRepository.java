package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.model.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
                    SELECT *
                    FROM users
                    WHERE id IN (:ids)
                    ORDER BY id
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true)
    List<User> findAllById(@Param("ids") List<Long> ids,
                           @Param("from") Long from,
                           @Param("size") Long size);

    @Query(value = """
                    SELECT *
                    FROM users
                    ORDER BY id
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true)
    List<User> findAll(@Param("from") Long from,
                       @Param("size") Long size);
}