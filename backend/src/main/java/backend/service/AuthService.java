package backend.service;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.dto.RoleDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.DataValidationException;
import backend.exception.ResourceAlreadyExistsException;
import backend.exception.ResourceNotFoundException;
import backend.repository.RoleEntityRepository;
import backend.repository.UserEntityRepository;
import backend.util.CustomDateFormatter;
import backend.util.LocationInfo;
import backend.util.ValidationUtil;
import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.IPGeolocationAPI;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static backend.constants.OtherConst.GEOLOCATION_API_KEY;
import static backend.constants.ResponseConst.USER_EMAIL_EXIST;
import static backend.constants.ResponseConst.USER_REGISTER_SUCCESSFULLY;
import static backend.constants.RoleConst.ADMIN_CONSTANT;
import static backend.constants.RoleConst.USER_CONSTANT;

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final UserEntityRepository userRepository;
    private final RoleEntityRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final CustomDateFormatter customDateFormatter;
    private final LocationInfo locationInfo;


    /**
     * Registers a new user with the provided RegisterDTO information.
     *
     * @param registerDto Registration data for the new user.
     * @return A success message if registration is successful.
     * @throws DataValidationException        if the registration data is not valid.
     * @throws ResourceAlreadyExistsException if the email already exists.
     */
    public String register(RegisterDTO registerDto) {
        if (!validationUtil.isValid(registerDto)) {
            throw new DataValidationException();
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return USER_EMAIL_EXIST;
        }

        UserEntity userEntity = modelMapper.map(registerDto, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userEntity.setDateOfBirth(customDateFormatter.convertToLocalDateFormat(registerDto.getDateOfBirth()));
        userEntity.setRegisterDate(customDateFormatter.formatLocalDateTimeNowAsString(LocalDateTime.now()));

        locationInfo.setUserLocations(userEntity);

        Set<RoleEntity> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            roles.add(roleRepository.findByName(ADMIN_CONSTANT));
        }
        RoleEntity userRole = roleRepository.findByName(USER_CONSTANT);
        roles.add(userRole);
        userEntity.setRoles(roles);

        userRepository.save(userEntity);
        return USER_REGISTER_SUCCESSFULLY;
    }


    /**
     * @param loginDto Login credentials including email and password.
     * @return AuthResponseDTO containing the information and user role(s) on successful login.
     * @throws DataValidationException if the login data is not valid.
     */
    public AuthResponseDTO login(LoginDTO loginDto) {
        if (!validationUtil.isValid(loginDto)) {
            throw new DataValidationException();
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity userEntity = userRepository.findByEmail(loginDto.getEmail());

        if (userEntity == null) {
            throw new ResourceNotFoundException();
        }

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        authResponseDTO.setEmail(loginDto.getEmail());
        authResponseDTO.setPassword(loginDto.getPassword());

        authResponseDTO.setRoles(userEntity
                .getRoles()
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet()));
        authResponseDTO.setFirstName(userEntity.getFirstName());

        if (!validationUtil.isValid(authResponseDTO)) {
            throw new DataValidationException();
        }

        return authResponseDTO;
    }

}
