package br.edu.fatecsjc.lgnspringapi.resource;

import br.edu.fatecsjc.lgnspringapi.dto.MarathonRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.MarathonResponseDTO;
import br.edu.fatecsjc.lgnspringapi.service.MarathonService;
import jakarta.validation.Valid; // Para usar as anotações de validação nos DTOs de Request
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/api/marathons") // Define a URL base para todos os endpoints deste controlador
public class MarathonResource { // Você pode chamar de MarathonController também, é comum

    private final MarathonService marathonService; // Injeção de dependência do serviço

    // Construtor para injeção de dependência
    public MarathonResource(MarathonService marathonService) {
        this.marathonService = marathonService;
    }

    /**
     * Endpoint para criar uma nova maratona.
     * Recebe um MarathonRequestDTO no corpo da requisição.
     * @param requestDTO DTO com os dados da nova maratona.
     * @return ResponseEntity com o MarathonResponseDTO da maratona criada e status 201 Created.
     */
    @PostMapping // Mapeia requisições POST para /api/marathons
    public ResponseEntity<MarathonResponseDTO> createMarathon(@Valid @RequestBody MarathonRequestDTO requestDTO) {
        // Delega a criação da maratona para a camada de serviço
        MarathonResponseDTO responseDTO = marathonService.createMarathon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Endpoint para listar todas as maratonas.
     * @return ResponseEntity com uma lista de MarathonResponseDTOs e status 200 OK.
     */
    @GetMapping // Mapeia requisições GET para /api/marathons
    public ResponseEntity<List<MarathonResponseDTO>> getAllMarathons() {
        // Delega a busca de todas as maratonas para a camada de serviço
        List<MarathonResponseDTO> responseDTOs = marathonService.getAllMarathons();
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Endpoint para buscar uma maratona pelo seu ID.
     * @param id O ID da maratona a ser buscada (extraído da URL).
     * @return ResponseEntity com o MarathonResponseDTO da maratona encontrada e status 200 OK.
     * Ou, se a maratona não for encontrada, um 404 Not Found (gerenciado pela exceção no serviço).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarathonResponseDTO> getMarathonById(@PathVariable Long id) {
        // Delega a busca da maratona por ID para a camada de serviço
        MarathonResponseDTO responseDTO = marathonService.getMarathonById(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para atualizar uma maratona existente.
     * @param id O ID da maratona a ser atualizada (extraído da URL).
     * @param requestDTO DTO com os dados atualizados da maratona.
     * @return ResponseEntity com o MarathonResponseDTO da maratona atualizada e status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MarathonResponseDTO> updateMarathon(
            @PathVariable Long id,
            @Valid @RequestBody MarathonRequestDTO requestDTO) {
        // Delega a atualização da maratona para a camada de serviço
        MarathonResponseDTO responseDTO = marathonService.updateMarathon(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para deletar uma maratona pelo seu ID.
     * @param id O ID da maratona a ser deletada (extraído da URL).
     * @return ResponseEntity sem corpo e status 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarathon(@PathVariable Long id) {
        // Delega a exclusão da maratona para a camada de serviço
        marathonService.deleteMarathon(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
