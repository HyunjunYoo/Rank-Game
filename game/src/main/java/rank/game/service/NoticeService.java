//package rank.game.service;
//
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import rank.game.dto.BoardDTO;
//import rank.game.dto.NoticeDTO;
//import rank.game.entity.BoardEntity;
//import rank.game.repository.BoardRepository;
//import rank.game.repository.CommentRepository;
//
//import java.io.File;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class NoticeService {
//
//    @Autowired
//    private NoticeRepository noticeRepository;
//
//    public Notice createNotice(Notice notice) {
//        notice.setCreatedAt(LocalDateTime.now());
//        notice.setActive(true);
//        return noticeRepository.save(notice);
//    }
//
//    public Notice updateNotice(Long id, Notice notice) {
//        Notice existingNotice = noticeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Notice not found"));
//        existingNotice.setTitle(notice.getTitle());
//        existingNotice.setContent(notice.getContent());
//        existingNotice.setUpdatedAt(LocalDateTime.now());
//        return noticeRepository.save(existingNotice);
//    }
//
//    public void deleteNotice(Long id) {
//        Notice notice = noticeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Notice not found"));
//        notice.setActive(false);
//        noticeRepository.save(notice);
//    }
//
//    public List<NoticeDTO> getAllNotices() {
//        return noticeRepository.findByIsActiveTrue();
//    }
//
//    public NoticeDTO getNoticeById(Long id) {
//        return noticeRepository.findById(id)
//
//    }
//}