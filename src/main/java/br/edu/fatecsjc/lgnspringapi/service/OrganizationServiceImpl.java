package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import br.edu.fatecsjc.lgnspringapi.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization createOrganization(Organization organization) {
        // Aqui você pode adicionar lógica de validação antes de salvar
        return organizationRepository.save(organization);
    }

    @Override
    public Optional<Organization> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public Organization updateOrganization(Long id, Organization organization) {
        Optional<Organization> existingOrganizationOptional = organizationRepository.findById(id);
        if (existingOrganizationOptional.isPresent()) {
            Organization existingOrganization = existingOrganizationOptional.get();
            // Atualiza os campos da organização existente com os valores da organização recebida
            existingOrganization.setName(organization.getName());
            existingOrganization.setAddress(organization.getAddress());
            existingOrganization.setInstitutionName(organization.getInstitutionName());
            existingOrganization.setHeadquartersCountry(organization.getHeadquartersCountry());
            // Para atualizar a lista de grupos, você precisará de uma lógica mais específica
            // dependendo de como você quer gerenciar essa atualização. Uma abordagem comum
            // é buscar os grupos existentes e comparar com os novos, adicionando e removendo conforme necessário.
            // Por enquanto, vamos omitir a atualização da lista de grupos aqui.
            return organizationRepository.save(existingOrganization);
        } else {
            throw new EntityNotFoundException("Organization with ID " + id + " not found");
        }
    }

    @Override
    public void deleteOrganization(Long id) {
        if (organizationRepository.existsById(id)) {
            organizationRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Organization with ID " + id + " not found");
        }
    }
}
