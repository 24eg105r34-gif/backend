package app.repository;

import app.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByActiveTrueOrderByPublishedDateDesc();
    List<Notice> findByCategoryAndActiveTrueOrderByPublishedDateDesc(Notice.NoticeCategory category);
    List<Notice> findTop5ByActiveTrueOrderByPublishedDateDesc();
}
