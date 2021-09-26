package se.kry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kry.model.ServiceIdentity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceIdentity, Long> {
    ServiceIdentity findServiceIdentitiesByUrl(String url);
}
