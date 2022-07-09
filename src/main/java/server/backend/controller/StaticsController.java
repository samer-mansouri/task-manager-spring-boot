package server.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.backend.repository.IModuleRepo;
import server.backend.repository.IProjectRepo;
import server.backend.repository.ITaskRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class StaticsController {

    @Autowired
    IModuleRepo moduleRepo;

    @Autowired
    IProjectRepo projectRepo;

    @Autowired
    ITaskRepo taskRepo;

    @RequestMapping("/statics")
    public void getStatics(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            Map<String, Long> statics = new HashMap<>();
            statics.put("modules", moduleRepo.count());
            statics.put("projects", projectRepo.count());
            statics.put("tasks", taskRepo.count());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), statics);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
