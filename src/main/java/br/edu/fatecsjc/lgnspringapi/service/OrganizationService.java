package br.edu.fatecsjc.lgnspringapi.service;

import br.edu.fatecsjc.lgnspringapi.entity.Organization;
import java.util.List;
import java.util.Optional;

public interface OrganizationService {

    Organization createOrganization(Organization organization);

    Optional<Organization> getOrganizationById(Long id);

    List<Organization> getAllOrganizations();

    Organization updateOrganization(Long id, Organization organization);

    void deleteOrganization(Long id);
}