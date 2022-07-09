package server.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.backend.entity.Module;

import java.util.List;


@Repository
public interface IModuleRepo extends JpaRepository<Module, Long> {


    List<Module> findByProjectId(Long projectId);
    Long countByProjectId(Long projectId);
    long count();
}
