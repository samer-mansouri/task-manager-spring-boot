package server.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.backend.entity.Module;
import server.backend.repository.IModuleRepo;
import server.backend.repository.IProjectRepo;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ModuleController {

    @Autowired
    IModuleRepo moduleRepo;

    @Autowired
    IProjectRepo projectRepo;

    @PostMapping("/module/{projectId}")
    public ResponseEntity<Module> save(@PathVariable Long projectId, @RequestBody Module module) {
        try {

            System.out.println("module: " + module);
            Module mod = projectRepo.findById(projectId)
                    .map(project -> {
                        module.setProject(project);
                        return moduleRepo.save(module);
                    }).orElseThrow(() -> new Exception("Project not found"));

            return new ResponseEntity<>(
                    mod
                    , HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/modules")
    public ResponseEntity<List<Module>> getAllModules(){
        try {
            List<Module> list = moduleRepo.findAll();
            if(list.isEmpty() || list.size() == 0){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return  new ResponseEntity<List<Module>>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/module/{id}")
    public ResponseEntity<Module> getModuleById(@RequestBody Long id){
        try {
            Optional<Module> module = moduleRepo.findById(id);
            if(module.isPresent()){
                return new ResponseEntity<>(module.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/module/{id}")
    public ResponseEntity<Module> updateModule(@RequestBody Module module, @PathVariable Long id){
        try {
            Optional<Module> module1 = moduleRepo.findById(id);
            if(module1.isPresent()){
                module.setId(id);
                return new ResponseEntity<>(moduleRepo.save(module), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/module/{id}")
    public ResponseEntity<Module> deleteModule(@PathVariable Long id){
        try {
            Optional<Module> module = moduleRepo.findById(id);
            if(module.isPresent()){
                moduleRepo.delete(module.get());
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get module by project Id
    @GetMapping("/module/{projectId}/project")
    public ResponseEntity<List<Module>> getModuleByProjectId(@PathVariable Long projectId){
        try {
            List<Module> list = moduleRepo.findByProjectId(projectId);
            if(list.isEmpty() || list.size() == 0){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Modules count by project Id
    @GetMapping("/module/{projectId}/count")
    public ResponseEntity<Long> getModuleCountByProjectId(@PathVariable Long projectId){
        try {
            return new ResponseEntity<>(moduleRepo.countByProjectId(projectId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
