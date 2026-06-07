package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilations.model.Compilation;

import java.util.List;

public interface CompilationsRepository extends JpaRepository<Compilation, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM compilations
                    WHERE :pinned IS NULL OR pinned = :pinned
                    ORDER BY id
                    LIMIT :size OFFSET :from
                    """,
            nativeQuery = true
    )
    List<Compilation> findByPinned(@Param("pinned") Boolean pinned,
                                   @Param("from") int from,
                                   @Param("size") int size);
}
