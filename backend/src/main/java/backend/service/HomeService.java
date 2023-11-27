package backend.service;

import backend.dto.UserDetailsDTO;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ValidateData validateData;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;

    public List<UserDetailsDTO> getAllUsers(String email, String password) {
        validateData.validateUserWithPassword(email, password);

        return userEntityRepository
                .findAll()
                .stream()
                .map(user -> modelMapper
                        .map(user, UserDetailsDTO.class))
                .collect(Collectors.toList());
    }


}
