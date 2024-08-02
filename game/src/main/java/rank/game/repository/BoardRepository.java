package rank.game.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rank.game.entity.BoardEntity;
import rank.game.entity.CommentEntity;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findByTitleContaining(String searchKeyword, Pageable pageable);

    // 이전글 찾기
    @Query("SELECT b FROM BoardEntity b WHERE b.createdAt < (SELECT b2.createdAt FROM BoardEntity b2 WHERE b2.id = :id) ORDER BY b.createdAt DESC")
    List<BoardEntity> findPreviousPosts(@Param("id") Long id, Pageable pageable);

    // 다음글 찾기
    @Query("SELECT b FROM BoardEntity b WHERE b.createdAt > (SELECT b2.createdAt FROM BoardEntity b2 WHERE b2.id = :id) ORDER BY b.createdAt ASC")
    List<BoardEntity> findNextPosts(@Param("id") Long id, Pageable pageable);


}
