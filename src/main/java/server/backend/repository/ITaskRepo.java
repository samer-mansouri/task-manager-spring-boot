package server.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.backend.entity.Task;

import java.util.List;

@Repository
public interface ITaskRepo extends JpaRepository<Task, Long> {

    List<Task> findByModuleId(Long moduleId);
    Long countByModuleId(Long moduleId);

    long count();


}