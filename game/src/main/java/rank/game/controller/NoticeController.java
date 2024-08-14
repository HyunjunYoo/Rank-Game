//package rank.game.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import rank.game.service.NoticeService;
//
//@Controller
//@RequestMapping("/notice")
//public class NoticeController {
//
//    @Autowired
//    private NoticeService noticeService;
//
//    @GetMapping
//    public String listNotices(Model model) {
//        model.addAttribute("notices", noticeService.getAllNotices());
//        return "notice/list";
//    }
//
//    @GetMapping("/{id}")
//    public String viewNotice(@PathVariable Long id, Model model) {
//        model.addAttribute("notice", noticeService.getNoticeById(id));
//        return "notice/view";
//    }
//
//    @GetMapping("/create")
//    public String createNoticeForm(Model model) {
//        model.addAttribute("notice", new Notice());
//        return "notice/create";
//    }
//
//    @PostMapping("/create")
//    public String createNotice(@ModelAttribute Notice notice) {
//        noticeService.createNotice(notice);
//        return "redirect:/notices";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editNoticeForm(@PathVariable Long id, Model model) {
//        model.addAttribute("notice", noticeService.getNoticeById(id));
//        return "notice/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String updateNotice(@PathVariable Long id, @ModelAttribute Notice notice) {
//        noticeService.updateNotice(id, notice);
//        return "redirect:/notices";
//    }
//
//    @PostMapping("/delete/{id}")
//    public String deleteNotice(@PathVariable Long id) {
//        noticeService.deleteNotice(id);
//        return "redirect:/notices";
//    }
//}
