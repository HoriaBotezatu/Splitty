package server.services.implementations;

import commons.Event;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.database.TagRepository;
import server.services.interfaces.TagService;

import java.util.List;

@Service
public class TagServiceImplementation implements TagService {

    private final TagRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    public TagServiceImplementation(TagRepository repo, SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public ResponseEntity<List<Tag>> getAll() {
        List<Tag> tagList = repo.findAll();
        if (tagList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(repo.findAll());
    }

    @Override
    public ResponseEntity<Tag> getById(int id) {
        var res = repo.findById(id);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Tag> updateById(int id, Tag newTag) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return repo.findById(id)
                .map(existingTag -> {
                    existingTag.setTitle(newTag.getTitle());
                    messagingTemplate.convertAndSend("/topic/event", existingTag);
                    return ResponseEntity.ok(repo.save(existingTag));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Tag> add(String text) {
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Tag saved = repo.save(new Tag(text));
        messagingTemplate.convertAndSend("/topic/event", saved);
        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<Tag> deleteById(int id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag = repo.findById(id).get();
        ResponseEntity<Tag> response = ResponseEntity.ok(tag);
        repo.deleteById(id);
        return response;
    }
}
