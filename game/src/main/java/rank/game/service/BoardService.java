package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rank.game.dto.BoardDTO;
import rank.game.entity.BoardEntity;
import rank.game.repository.BoardRepository;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired // Autowired를 사용하면 BoardApplication의 @Bean이 자동적으로 읽어준다.
    private BoardRepository boardRepository;

    // 게시글 저장
    public void boardWrite(BoardEntity board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board); // 새 게시글 저장
    }


    // 게시글 리스트 처리
    public Page<BoardEntity> boardList(Pageable pageable) {return boardRepository.findAll(pageable);}

    // 게시글 검색 처리
    public Page<BoardEntity> boardSearchList(String searchKeyword, Pageable pageable) {return boardRepository.findByTitleContaining(searchKeyword, pageable);}

    // 특정 게시글 불러오기
    public BoardDTO boardView(Long id) {
        // 게시글 조회
        BoardEntity board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        // Entity를 DTO로 변환하여 반환
        return BoardDTO.fromEntity(board);
    }

    // 특정 게시글 삭제
    public void boardDelete(Long id) {
        boardRepository.deleteById(id);
    }

    // 게시글 업데이트
    public void updateBoard(BoardEntity board) {
        boardRepository.save(board); // 기존 게시글 업데이트
    }

    // 게시물 찾기
    public BoardEntity getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 이전글 찾기
    public BoardEntity getPreviousPost(Long id) {
        List<BoardEntity> posts = boardRepository.findPreviousPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }

    // 다음글 찾기
    public BoardEntity getNextPost(Long id) {
        List<BoardEntity> posts = boardRepository.findNextPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }
}
