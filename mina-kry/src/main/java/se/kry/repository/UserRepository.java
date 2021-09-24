package se.kry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kry.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
