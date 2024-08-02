package rank.game.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rank.game.entity.BoardEntity;
import rank.game.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByBoard(BoardEntity board);  // 수정된 메서드 시그니처

    // 댓글을 수정하는 메서드
    @Modifying
    @Query("UPDATE CommentEntity c SET c.content = :content, c.updatedAt = :updatedAt WHERE c.id = :id AND c.member.id = :memberId")
    int updateComment(@Param("id") Long id, @Param("content") String content, @Param("updatedAt") LocalDateTime updatedAt, @Param("memberId") Long memberId);

    // 댓글을 삭제하는 메서드
    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.id = :id AND c.member.id = :memberId")
    int deleteComment(@Param("id") Long id, @Param("memberId") Long memberId);

}