package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.dto.CommentDTO;
import rank.game.service.CommentService;

import javax.xml.stream.events.Comment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addComment(@RequestBody CommentDTO commentDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        // 로그인 세션 확인
        if (session.getAttribute("loginEmail") == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 댓글 내용 검증
        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            response.put("message", "댓글을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        // 댓글 추가
        commentService.addComment(commentDTO);
        response.put("message", "댓글이 성공적으로 추가되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(@PathVariable Long boardId) {
        List<CommentDTO> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            commentService.updateComment(commentId, memberId, commentDTO.getContent());
            response.put("message", "댓글이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            commentService.deleteComment(commentId, memberId);
            response.put("message", "댓글이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
