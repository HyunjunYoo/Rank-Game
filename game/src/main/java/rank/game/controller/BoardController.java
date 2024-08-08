package rank.game.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rank.game.dto.BoardDTO;
import rank.game.dto.MemberDTO;
import rank.game.entity.BoardEntity;
import rank.game.service.BoardService;
import rank.game.service.MemberService;

import java.io.File;
import java.util.UUID;


@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;

    public BoardController(BoardService boardService, MemberService memberService) {
        this.boardService = boardService;
        this.memberService = memberService;
    }

    // 게시판 리스트로 이동
    @GetMapping
    public String boardList(Model model, HttpSession session,
                            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword) {

        // 페이지는 0부터 시작하므로 -1 해줍니다.
        Pageable currentPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        Page<BoardEntity> pageResult;
        if (searchKeyword.isEmpty()) {
            pageResult = boardService.boardList(currentPageable);
        } else {
            pageResult = boardService.boardSearchList(searchKeyword, currentPageable);
        }

        int nowPage = pageResult.getNumber() + 1;
        int startPage = Math.max(nowPage - 2, 1);
        int endPage = Math.min(nowPage + 3, pageResult.getTotalPages());

        model.addAttribute("list", pageResult);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("searchKeyword", searchKeyword);

        return "html/board/boardList";
    }


    // 게시글 쓰기 페이지 이동
    @GetMapping("/write")
    public String createForm(Model model, HttpSession session) {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;


        // 로그인 성공 시
        if(session.getAttribute("loginEmail") != null) {
            model.addAttribute("isLogin", isLogin);
            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("loginEmail", session.getAttribute("loginEmail"));
            return "html/board/boardWrite";
        } else {
            // 로그인 실패
            model.addAttribute("isLogin", false);
            model.addAttribute("message", "로그인 이후 게시글 작성을 이용하실 수 있습니다.");
            model.addAttribute("searchUrl", "/board");
            return "html/message";
        }
    }

    // 글작성 처리
    @PostMapping("/writepro")
    public String boardWritePro(BoardEntity board, Model model, MultipartFile file, HttpSession session) throws Exception {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        String email = (String) session.getAttribute("loginEmail");
        String nickname = (String) session.getAttribute("nickname");
        board.setMemberEmail(email);
        board.setNickname(nickname);

        if (file != null && !file.isEmpty()) {
            System.out.println("Controller filename: " + file.getOriginalFilename());
        } else {
            System.out.println("No file uploaded");
        }

        boardService.boardWrite(board, file);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board");

        return "html/message"; // 또는 필요한 페이지로 리다이렉트
    }

    // 특정 게시글 불러오기
    @GetMapping("/view/{id}")
    public String boardView(@PathVariable Long id, Model model,
                            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword,
                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                            HttpSession session) {

        BoardDTO board = boardService.boardView(id);

        if (board == null) {
            model.addAttribute("message", "해당 게시글을 찾을 수 없습니다.");
            return "redirect:/board/list";  // 게시글 목록 페이지로 리다이렉트
        }

        // 로그인 이메일을 세션에서 가져오기
        String loginEmail = (String) session.getAttribute("loginEmail");
        Long memberNum = (Long) session.getAttribute("memberNum");
        String nickname = (String) session.getAttribute("nickname");

        // 이전글과 다음글 정보 가져오기
        BoardEntity previousPost = boardService.getPreviousPost(id);
        BoardEntity nextPost = boardService.getNextPost(id);

        // 조회수 증가 로직
        String sessionKey = "viewed_board_" + id;
        if (session.getAttribute(sessionKey) == null) {
            boardService.incrementViewCount(id);
            session.setAttribute(sessionKey, true);
        }

        // 특정 게시글 가져오기
        model.addAttribute("board", board);
        model.addAttribute("createdAt", board.getCreatedAt());
        model.addAttribute("nickname", nickname);
        model.addAttribute("memberNum", memberNum);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("page", page);


        // 이전글과 다음글 정보 추가
        model.addAttribute("previousPost", previousPost);
        model.addAttribute("nextPost", nextPost);

        // 로그인 상태면 true
        model.addAttribute("isLogin", loginEmail != null);

        return "html/board/boardView";
    }

    // 게시글 삭제
    @GetMapping("/delete/{id}")
    public String boardDelete(@PathVariable Long id, BoardEntity board, Model model, HttpSession session) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        BoardEntity boardTemp = BoardEntity.toBoardEntity(boardService.boardView(id));

        // 삭제 권한 확인
        if (isLogin && boardTemp.getMemberEmail().equals(session.getAttribute("loginEmail").toString())) {
            boardService.boardDelete(id);
            model.addAttribute("message", "글 삭제가 완료되었습니다.");
            model.addAttribute("searchUrl", "/board");
            return "html/message";
        } else {
            model.addAttribute("message", "게시글 삭제 권한이 없습니다.");
            model.addAttribute("searchUrl", "/board");
            return "html/message";
        }
    }

    // 게시글 수정
    @GetMapping("/modify/{id}")
    public String boardModify(@PathVariable Long id, Model model, HttpSession session) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        // 게시글 조회
        BoardEntity boardTemp = boardService.getBoardById(id);

        // 수정 권한 확인
        if (isLogin && boardTemp.getMemberEmail().equals(session.getAttribute("loginEmail").toString())) {
            model.addAttribute("board", boardTemp);
            return "html/board/boardModify";
        } else {
            model.addAttribute("message", "게시글 수정 권한이 없습니다.");
            model.addAttribute("searchUrl", "/board");
            return "html/message";
        }
    }

    // 게시글 수정 처리
    @PostMapping("/update/{id}")
    public String boardUpdate(@PathVariable Long id, BoardEntity board, Model model, MultipartFile file, HttpSession session) throws Exception {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        // 게시글 조회
        BoardEntity boardTemp = boardService.getBoardById(id);

        // 수정 권한 확인
        if (isLogin && boardTemp.getMemberEmail().equals(session.getAttribute("loginEmail").toString())) {
            // 제목과 내용 수정
            boardTemp.setTitle(board.getTitle());
            boardTemp.setContent(board.getContent());

            // 파일 처리
            if (file != null && !file.isEmpty()) {
                String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
                UUID uuid = UUID.randomUUID();
                String fileName = uuid.toString() + "_" + file.getOriginalFilename();
                File saveFile = new File(projectPath, fileName);
                file.transferTo(saveFile);

                boardTemp.setFilename(fileName);
                boardTemp.setFilepath("/files/" + fileName);
            }

            // 게시글 업데이트
            boardService.updateBoard(boardTemp);

            model.addAttribute("message", "글 수정이 완료되었습니다.");
            model.addAttribute("searchUrl", "/board");
        } else {
            model.addAttribute("message", "게시글 수정 권한이 없습니다.");
            model.addAttribute("searchUrl", "/board");
        }

        return "html/message";
    }
}
