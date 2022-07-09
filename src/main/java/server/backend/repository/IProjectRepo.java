package server.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.backend.entity.Project;

@Repository
public interface IProjectRepo extends JpaRepository<Project, Long> {

    long count();

}
