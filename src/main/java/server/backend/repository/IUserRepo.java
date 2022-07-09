package server.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.backend.entity.User;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);

}