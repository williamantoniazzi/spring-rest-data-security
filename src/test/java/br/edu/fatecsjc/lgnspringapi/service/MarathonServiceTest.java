package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.dto.MarathonRequestDTO;
import br.edu.fatecsjc.lgnspringapi.dto.MarathonResponseDTO;
import br.edu.fatecsjc.lgnspringapi.entity.Marathon;
import br.edu.fatecsjc.lgnspringapi.exception.ResourceNotFoundException;
import br.edu.fatecsjc.lgnspringapi.repository.MarathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarathonService Tests")
class MarathonServiceTest {

    @Mock
    private MarathonRepository marathonRepository;

    @InjectMocks
    private MarathonService marathonService;

    private Marathon sampleMarathon;
    private MarathonRequestDTO sampleRequestDTO;
    private MarathonResponseDTO sampleResponseDTO;

    @BeforeEach
    void setUp() {
        sampleMarathon = Marathon.builder()
                .id(1L)
                .identification("Sample Marathon")
                .weight(70.0)
                .score(8.5)
                .build();

        sampleRequestDTO = MarathonRequestDTO.builder()
                .identification("New Marathon")
                .weight(60.0)
                .score(7.0)
                .build();

        sampleResponseDTO = MarathonResponseDTO.builder()
                .id(1L)
                .identification("Sample Marathon")
                .weight(70.0)
                .score(8.5)
                .build();
    }

    @Test
    @DisplayName("Should create a marathon successfully")
    void shouldCreateMarathonSuccessfully() {
        // Given
        Marathon marathonToSave = sampleRequestDTO.toEntity();// Entity without ID
        Marathon savedMarathon = sampleMarathon; // Entity with ID

        when(marathonRepository.save(any(Marathon.class))).thenReturn(savedMarathon);

        // When
        MarathonResponseDTO result = marathonService.createMarathon(sampleRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(sampleResponseDTO.getId(), result.getId());
        assertEquals(sampleResponseDTO.getIdentification(), result.getIdentification());
        assertEquals(sampleResponseDTO.getWeight(), result.getWeight());
        assertEquals(sampleResponseDTO.getScore(), result.getScore());
        verify(marathonRepository, times(1)).save(any(Marathon.class));
    }

    @Test
    @DisplayName("Should get all marathons successfully")
    void shouldGetAllMarathonsSuccessfully() {
        // Given
        List<Marathon> marathons = Arrays.asList(
                sampleMarathon,
                Marathon.builder().id(2L).identification("Marathon 2").weight(80.0).score(9.0).build()
        );
        when(marathonRepository.findAll()).thenReturn(marathons);

        // When
        List<MarathonResponseDTO> result = marathonService.getAllMarathons();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sample Marathon", result.get(0).getIdentification());
        assertEquals("Marathon 2", result.get(1).getIdentification());
        verify(marathonRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get marathon by ID successfully")
    void shouldGetMarathonByIdSuccessfully() {
        // Given
        when(marathonRepository.findById(1L)).thenReturn(Optional.of(sampleMarathon));

        // When
        MarathonResponseDTO result = marathonService.getMarathonById(1L);

        // Then
        assertNotNull(result);
        assertEquals(sampleResponseDTO.getId(), result.getId());
        assertEquals(sampleResponseDTO.getIdentification(), result.getIdentification());
        verify(marathonRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when marathon not found by ID")
    void shouldThrowExceptionWhenMarathonNotFoundById() {
        // Given
        when(marathonRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            marathonService.getMarathonById(99L);
        });
        assertEquals("Marathon com ID 99 não encontrada.", thrown.getMessage());
        verify(marathonRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should update a marathon successfully")
    void shouldUpdateMarathonSuccessfully() {
        // Given
        MarathonRequestDTO updateRequest = MarathonRequestDTO.builder()
                .identification("Updated Marathon")
                .weight(72.0)
                .score(9.0)
                .build();
        Marathon updatedMarathon = Marathon.builder()
                .id(1L)
                .identification("Updated Marathon")
                .weight(72.0)
                .score(9.0)
                .build();

        when(marathonRepository.findById(1L)).thenReturn(Optional.of(sampleMarathon));
        when(marathonRepository.save(any(Marathon.class))).thenReturn(updatedMarathon);

        // When
        MarathonResponseDTO result = marathonService.updateMarathon(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Marathon", result.getIdentification());
        assertEquals(72.0, result.getWeight());
        assertEquals(9.0, result.getScore());
        verify(marathonRepository, times(1)).findById(1L);
        verify(marathonRepository, times(1)).save(sampleMarathon); // should save the existing object after modification
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent marathon")
    void shouldThrowExceptionWhenUpdatingNonExistentMarathon() {
        // Given
        when(marathonRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            marathonService.updateMarathon(99L, sampleRequestDTO);
        });
        assertEquals("Marathon com ID 99 não encontrada para atualização.", thrown.getMessage());
        verify(marathonRepository, times(1)).findById(99L);
        verify(marathonRepository, never()).save(any(Marathon.class));
    }

    @Test
    @DisplayName("Should delete a marathon successfully")
    void shouldDeleteMarathonSuccessfully() {
        // Given
        when(marathonRepository.existsById(1L)).thenReturn(true);
        doNothing().when(marathonRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> marathonService.deleteMarathon(1L));

        // Then
        verify(marathonRepository, times(1)).existsById(1L);
        verify(marathonRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent marathon")
    void shouldThrowExceptionWhenDeletingNonExistentMarathon() {
        // Given
        when(marathonRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            marathonService.deleteMarathon(99L);
        });
        assertEquals("Marathon com ID 99 não encontrada para exclusão.", thrown.getMessage());
        verify(marathonRepository, times(1)).existsById(99L);
        verify(marathonRepository, never()).deleteById(anyLong());
    }
}