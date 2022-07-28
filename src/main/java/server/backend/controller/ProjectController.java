package server.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.backend.entity.Project;
import server.backend.filter.IAuthenticationFacade;
import server.backend.filter.TokenAccessor;
import server.backend.repository.IProjectRepo;
import server.backend.repository.IUserRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ProjectController {

    @Autowired
    IProjectRepo projectRepo;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Autowired
    IUserRepo userRepo;

    private static final TokenAccessor tokenAccessor = new TokenAccessor();

    @PostMapping("/project")
    public void save(HttpServletRequest request, HttpServletResponse response, @RequestHeader("Authorization") String token) throws IOException {
        try {
            Long chiefId = tokenAccessor.accessTokenId(token);
            String titre = request.getParameter("titre");
            String description = request.getParameter("description");
            String etat = request.getParameter("etat");
            Date dateDebut = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dateDebut"));
            Date dateFin = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dateFin"));

            Project project = new Project(titre, dateDebut, dateFin, description, etat);
            try {
                userRepo.findById(chiefId).ifPresent(project::setChief);
            } catch (Exception e) {
                new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            Project savedProject = projectRepo.save(project);
            Map<String, Object> map = new HashMap<>();
            map.put("project", savedProject);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("error", e.getMessage());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), map);
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
