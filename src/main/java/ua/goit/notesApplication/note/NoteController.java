package ua.goit.notesApplication.note;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.goit.notesApplication.errorHandling.TitleAlreadyExistsException;
import ua.goit.notesApplication.user.UserDto;
import ua.goit.notesApplication.user.UserService;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping(path = "/note")
public class NoteController {

    private UserService userService;
    private NoteService noteService;

    @Autowired
    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }


    @GetMapping("/list")
    public String getNotes(Authentication authentication, Map<String, Object> model, HttpServletRequest request) {
        //List<NoteDto> all = noteService.getAll();
        UserDto userDto = userService.findUserByUsername(authentication.getName());
        List<NoteDto> notes = noteService.getListNotes(userDto.getId());
        model.put("notes", notes);
        //model.put("allNotes", all);
        String requestUrl = request.getRequestURL().toString();
        String shareUrl = requestUrl.replace("list", "share/");
        model.put("share", shareUrl);

        return "noteslist";
    }

    @GetMapping("/create")
    public String noteCreateForm(Model model) {
        model.addAttribute("note", new NoteDto());
        return "notecreateform";
    }

    @PostMapping("/create")
    public String noteAdd(Authentication authentication, @ModelAttribute("note") @Valid NoteDto noteDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "notecreateform";
        }
        try {
            UserDto userDto = userService.findUserByUsername(authentication.getName());
            noteDto.setUser(userDto);
            noteService.createNote(noteDto);
        } catch (TitleAlreadyExistsException ex) {
            model.addAttribute("message", ex.getMessage());
            return "notecreateform";
        }
        return "redirect:/note/list";
    }

    @PostMapping("/edit")
    public String noteEdit(Authentication authentication, @ModelAttribute("note") @Valid NoteDto noteDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "noteeditform";
        }
        try {
            UserDto userDto = userService.findUserByUsername(authentication.getName());
            noteDto.setUser(userDto);
            noteService.createNote(noteDto);
        } catch (TitleAlreadyExistsException ex) {
            model.addAttribute("message", ex.getMessage());
            return "noteeditform";
        }
        return "redirect:/note/list";
    }

    @GetMapping("/edit/{id}")
    public String noteEditForm(@PathVariable("id") UUID id, Map<String, Object> model) {

        NoteDto noteDto = noteService.findById(id);
        model.put("note", noteDto);
        return "noteeditform";
    }

    @GetMapping("/edit")
    public String noteEditWithParameter(@RequestParam("id") String id, Map<String, Object> model) {

        UUID uuid = UUID.fromString(id);
        NoteDto noteDto = noteService.findById(uuid);
        model.put("note", noteDto);
        return "noteeditform";
    }

    @GetMapping("delete/{id}")
    public String deleteNote(@PathVariable UUID id) {

        noteService.delete(id);
        return "redirect:/note/list";

    }

    @GetMapping("share/{id}")
    public String noteShow(Authentication authentication, @PathVariable String id, Map<String, Object> model) {
        final Optional<NoteDao> note;
        try {
            note = noteService.findByIdOptional(id);
        } catch (IllegalArgumentException ex) {
            model.put("message", "link entered incorrectly");
            return "errornote";
        }

        if ((note.isPresent() && (authentication != null)) ||
                (authentication == null && note.isPresent())) {
            if (note.get().getAccessType().equals(NoteAccessType.PUBLIC)) {
                model.put("note", note.get());
                return "sharednote";
            } else {
                model.put("message", "We can't find this note ");
                return "errornote";
            }
        } else {
            model.put("message", "We can't find this note ");
            return "errornote";
        }
    }

    @ModelAttribute
    public NoteDto getDefaultNoteDto() {
        return new NoteDto();
    }
}
