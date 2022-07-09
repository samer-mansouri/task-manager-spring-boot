package server.backend.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.backend.entity.Project;
import server.backend.filter.IAuthenticationFacade;
import server.backend.filter.TokenAccessor;
import server.backend.repository.IProjectRepo;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ProjectController {

    @Autowired
    IProjectRepo projectRepo;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @PostMapping("/project")
    public ResponseEntity<Project> save(@RequestBody Project project) {
        try {
            return new ResponseEntity<>(projectRepo.save(project), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<String> getAllProjects(HttpServletRequest request) {
        try {
            //Long userId = new TokenAccessor().accessTokenId(token);
            String email =  authenticationFacade.getAuthentication().getName();

            List<Project> list = projectRepo.findAll();
            if(list.isEmpty() || list.size() == 0){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return  new ResponseEntity<>(email, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProjectById(@RequestBody Long id){
        try {
            Optional<Project> project = projectRepo.findById(id);
            if(project.isPresent()){
                return new ResponseEntity<>(project.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<Project> updateProject(@RequestBody Project project, @PathVariable Long id){
        try {
            Optional<Project> project1 = projectRepo.findById(id);
            if(project1.isPresent()){
                project.setId(id);
                return new ResponseEntity<>(projectRepo.save(project), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable Long id){
        try {
            Optional<Project> project = projectRepo.findById(id);
            if(project.isPresent()){
                projectRepo.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
