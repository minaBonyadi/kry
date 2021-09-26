package se.kry.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kry.dto.ServiceIdentityDto;
import se.kry.exception.PollWebException;
import se.kry.service.PollService;
import se.kry.service.WebServices;

@RestController
@RequestMapping("/service")
@AllArgsConstructor
public class ServicePollerController {

    private final PollService pollService;
    private final WebServices webServiceManagement;

    @PostMapping("/poll/health-check")
    public ResponseEntity<String> healthCheckOfServices() {
        try {
            pollService.pollHandler();
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            throw new PollWebException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    @GetMapping("/get/{serviceId}")
    public ResponseEntity<ServiceIdentityDto> getServiceById(@PathVariable long serviceId) {
        try {
            return new ResponseEntity<>(webServiceManagement.getServiceById(serviceId), HttpStatus.OK);
        }catch (Exception e) {
            throw new PollWebException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/create/{userId}")
    public ResponseEntity<ServiceIdentityDto> createServiceById(@PathVariable long userId, @RequestBody ServiceIdentityDto serviceIdentityDto) {
        try {
             return new ResponseEntity<>(webServiceManagement.createService(userId, serviceIdentityDto), HttpStatus.OK);
        }catch (Exception e) {
            throw new PollWebException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ServiceIdentityDto> updateService(@RequestBody ServiceIdentityDto serviceIdentityDto) {
        try {
            return new  ResponseEntity<>(webServiceManagement.updateService(serviceIdentityDto), HttpStatus.OK);
        }catch (Exception e) {
            throw new PollWebException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ServiceIdentityDto> deleteService(@RequestBody ServiceIdentityDto serviceIdentityDto) {
        try {
            return new ResponseEntity<>(webServiceManagement.deleteService(serviceIdentityDto), HttpStatus.OK);
        }catch (Exception e) {
            throw new PollWebException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
