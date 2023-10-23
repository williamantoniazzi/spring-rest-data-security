package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.GroupDTO;
import br.edu.fatecsjc.lgnspringapi.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupResource {
    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> update(@PathVariable Long id, @RequestBody GroupDTO body) {
        return ResponseEntity.status(HttpStatusCode.valueOf(201))
                .body(groupService.save(id, body));
    }

    @PostMapping
    public ResponseEntity<GroupDTO> register(@RequestBody GroupDTO body) {
        return ResponseEntity.status(HttpStatusCode.valueOf(201))
                .body(groupService.save(body));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
