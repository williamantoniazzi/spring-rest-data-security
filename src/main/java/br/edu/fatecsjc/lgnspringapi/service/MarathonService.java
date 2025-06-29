package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.dto.MarathonRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.MarathonResponseDTO;
import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import br.edu.fatecsjc.lgnspringapi.repository.MarathonRepository;
import br.edu.fatecsjc.lgnspringapi.exception.ResourceNotFoundException; // Precisamos de uma exceção para "não encontrado"
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marca esta classe como um componente de serviço do Spring
public class MarathonService {

    private final MarathonRepository marathonRepository; // Injeção de dependência do repositório

    // Construtor para injeção de dependência (boa prática)
    public MarathonService(MarathonRepository marathonRepository) {
        this.marathonRepository = marathonRepository;
    }

    /**
     * Cria uma nova maratona no sistema.
     * @param requestDTO DTO com os dados para criação da maratona.
     * @return DTO de resposta da maratona criada.
     */
    @Transactional // Garante que a operação seja transacional (tudo ou nada)
    public MarathonResponseDTO createMarathon(MarathonRequestDTO requestDTO) {
        // Converte o DTO de requisição para a entidade JPA
        Marathon marathon = requestDTO.toEntity();
        // Salva a entidade no banco de dados
        Marathon savedMarathon = marathonRepository.save(marathon);
        // Converte a entidade salva de volta para DTO de resposta
        return MarathonResponseDTO.fromEntity(savedMarathon);
    }

    /**
     * Busca todas as maratonas cadastradas.
     * @return Uma lista de DTOs de resposta de maratonas.
     */
    @Transactional(readOnly = true) // Otimiza a transação para operações somente de leitura
    public List<MarathonResponseDTO> getAllMarathons() {
        // Busca todas as entidades Marathon no banco de dados
        List<Marathon> marathons = marathonRepository.findAll();
        // Converte cada entidade para um DTO de resposta e coleta em uma lista
        return marathons.stream()
                .map(MarathonResponseDTO::fromEntity)
                .collect(Collectors.toList()).stream().toList();
    }

    /**
     * Busca uma maratona pelo seu ID.
     * @param id O ID da maratona a ser buscada.
     * @return DTO de resposta da maratona encontrada.
     * @throws ResourceNotFoundException Se a maratona não for encontrada.
     */
    @Transactional(readOnly = true)
    public MarathonResponseDTO getMarathonById(Long id) {
        // Busca a maratona pelo ID, ou lança uma exceção se não encontrar
        Marathon marathon = marathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marathon com ID " + id + " não encontrada."));
        // Converte a entidade encontrada para DTO de resposta
        return MarathonResponseDTO.fromEntity(marathon);
    }

    /**
     * Atualiza uma maratona existente.
     * @param id O ID da maratona a ser atualizada.
     * @param requestDTO DTO com os novos dados da maratona.
     * @return DTO de resposta da maratona atualizada.
     * @throws ResourceNotFoundException Se a maratona a ser atualizada não for encontrada.
     */
    @Transactional
    public MarathonResponseDTO updateMarathon(Long id, MarathonRequestDTO requestDTO) {
        // Primeiro, verifica se a maratona existe
        Marathon existingMarathon = marathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marathon com ID " + id + " não encontrada para atualização."));

        // Atualiza os campos da entidade existente com os dados do DTO
        existingMarathon.setIdentification(requestDTO.getIdentification());
        existingMarathon.setWeight(requestDTO.getWeight());
        existingMarathon.setScore(requestDTO.getScore());
        // Se houver mais campos que possam ser atualizados, adicione-os aqui

        // Salva a entidade atualizada (o JPA fará um UPDATE no banco)
        Marathon updatedMarathon = marathonRepository.save(existingMarathon);
        // Converte e retorna o DTO de resposta
        return MarathonResponseDTO.fromEntity(updatedMarathon);
    }

    /**
     * Deleta uma maratona pelo seu ID.
     * @param id O ID da maratona a ser deletada.
     * @throws ResourceNotFoundException Se a maratona a ser deletada não for encontrada.
     */
    @Transactional
    public void deleteMarathon(Long id) {
        // Verifica se a maratona existe antes de tentar deletar
        if (!marathonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marathon com ID " + id + " não encontrada para exclusão.");
        }
        // Deleta a entidade do banco de dados
        marathonRepository.deleteById(id);
    }
}
