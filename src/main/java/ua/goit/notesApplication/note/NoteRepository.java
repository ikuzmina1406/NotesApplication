package ua.goit.notesApplication.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<NoteDao, UUID> {

    @Query("SELECT c FROM NoteDao c WHERE (c.user.id=?1)")
    List<NoteDao> findByUserId(UUID uuid);

    @Query("SELECT c FROM NoteDao c WHERE (c.title=?1)")
    Optional<NoteDao> findByTitle(String title);
}

