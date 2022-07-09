package server.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.backend.entity.Task;
import server.backend.repository.IModuleRepo;
import server.backend.repository.ITaskRepo;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

        @Autowired
        ITaskRepo taskRepo;

        @Autowired
        IModuleRepo moduleRepo;

        @PostMapping("/task/{moduleId}")
        public ResponseEntity<Task> save(@PathVariable Long moduleId, @RequestBody Task task) {
            try {
                Task tas = moduleRepo.findById(moduleId)
                        .map(module -> {
                            task.setModule(module);
                            return taskRepo.save(task);
                        }).orElseThrow(() -> new Exception("Module not found"));
                return new ResponseEntity<>(tas, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        @GetMapping("/tasks")
        public ResponseEntity<List<Task>> getAllTasks(){
            try {
                List<Task> list = taskRepo.findAll();
                if(list.isEmpty() || list.size() == 0){
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return  new ResponseEntity<List<Task>>(list, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @GetMapping("/task/{id}")
        public ResponseEntity<Task> getTaskById(@RequestBody Long id){
            try {
                Optional<Task> task = taskRepo.findById(id);
                if(task.isPresent()){
                    return new ResponseEntity<>(task.get(), HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PutMapping("/task/{id}")
        public ResponseEntity<Task> updateTask(@RequestBody Task task, @PathVariable Long id) {
            try {
                Optional<Task> task1 = taskRepo.findById(id);
                if (task1.isPresent()) {
                    task.setId(id);
                    return new ResponseEntity<>(taskRepo.save(task), HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @DeleteMapping("/task/{id}")
        public ResponseEntity<Task> deleteTask(@PathVariable Long id) {
            try {
                Optional<Task> task = taskRepo.findById(id);
                if (task.isPresent()) {
                    taskRepo.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @GetMapping("/task/{moduleId}")
        public ResponseEntity<List<Task>> getTasksByModuleId(@PathVariable Long moduleId){
                try {
                    List<Task> list = taskRepo.findByModuleId(moduleId);
                    if(list.isEmpty() || list.size() == 0){
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                    return  new ResponseEntity<List<Task>>(list, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        @GetMapping("/task/{moduleId}/count")
        public ResponseEntity<Long> getCountTasksByModuleId(@PathVariable Long moduleId){
                try {
                    Long count = taskRepo.countByModuleId(moduleId);
                    if(count == 0){
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                    return  new ResponseEntity<Long>(count, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
}
