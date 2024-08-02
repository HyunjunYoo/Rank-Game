package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.CommentDTO;
import rank.game.entity.BoardEntity;
import rank.game.entity.CommentEntity;
import rank.game.entity.MemberEntity;
import rank.game.repository.CommentRepository;
import rank.game.repository.mybatis.CommentMapper;
import rank.game.repository.BoardRepository;
import rank.game.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentRepository commentRepository;

    public void addComment(CommentDTO commentDTO) {

        // DTO에서 ID를 가져와 BoardRepository에서 게시물을 조회합
        BoardEntity board = boardRepository.findById(commentDTO.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + commentDTO.getBoardId()));


        MemberEntity member = memberRepository.findById(commentDTO.getMemberNum())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + commentDTO.getMemberNum()));

        CommentEntity commentEntity = CommentEntity.toEntity(commentDTO, board, member);

        commentMapper.insertComment(commentEntity);
    }

    public List<CommentDTO> getCommentsByBoardId(Long boardId) {
        List<CommentEntity> commentEntities = commentMapper.selectCommentsByBoardId(boardId);

        return commentEntities.stream().map(CommentDTO::fromEntity).collect(Collectors.toList());
    }

    public void updateComment(Long commentId, Long memberId, String content) {
        int updatedCount = commentRepository.updateComment(commentId, content, LocalDateTime.now(), memberId);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("Comment not found or not authorized to update.");
        }
    }

    public void deleteComment(Long commentId, Long memberId) {
        int deletedCount = commentRepository.deleteComment(commentId, memberId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("Comment not found or not authorized to delete.");
        }
    }
}