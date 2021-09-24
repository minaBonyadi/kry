package se.kry.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import se.kry.dto.ServiceIdentityDto;
import se.kry.exception.PollServiceException;
import se.kry.model.ServiceIdentity;
import se.kry.model.User;
import se.kry.repository.ServiceRepository;
import se.kry.repository.UserRepository;

@Service
@AllArgsConstructor
public class WebServices {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceIdentityDto getServiceById(long id) {
        ServiceIdentity service = serviceRepository.findById(id).orElseThrow(()->
                new PollServiceException(String.format("service_id %s not found", id)));

        return ServiceIdentityDto.builder()
                .id(service.getId())
                .url(service.getUrl())
                .status(service.getStatus()).build();
    }

    public ServiceIdentityDto createService(long userId, ServiceIdentityDto service) {
        ServiceIdentity newService = new ServiceIdentity();

        if (userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).orElseThrow(() ->
                   new PollServiceException(String.format("user_id %s not found", userId)));

            newService.setUser(user);
            newService.setUrl(service.getUrl());
            serviceRepository.save(newService);

            return ServiceIdentityDto.builder()
                    .id(newService.getId())
                    .url(newService.getUrl())
                    .status(newService.getStatus()).build();
        }
        return null;
    }

    public ServiceIdentityDto updateService(ServiceIdentityDto service) {
        ServiceIdentity updatedService = serviceRepository.findById(service.getId())
                .orElseThrow(() -> new PollServiceException(String.format("service_id %s not found", service.getId())));

        updatedService.setUrl(service.getUrl());
        serviceRepository.save(updatedService);

        return ServiceIdentityDto.builder()
                .id(updatedService.getId())
                .url(updatedService.getUrl())
                .status(updatedService.getStatus()).build();
    }

    public ServiceIdentityDto deleteService(ServiceIdentityDto service) {
        ServiceIdentity deletedService = serviceRepository.findById(service.getId())
                .orElseThrow(() -> new PollServiceException(String.format("service_id %s not found", service.getId())));

        serviceRepository.delete(deletedService);

        return ServiceIdentityDto.builder()
                .id(deletedService.getId())
                .url(deletedService.getUrl())
                .status(deletedService.getStatus()).build();
    }
}
