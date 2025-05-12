package user.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.management.domain.entity.UserEntity;
import user.management.domain.repository.UserRepository;
import user.management.model.enums.UserStatus;
import user.management.exception.AlreadyExistsException;
import user.management.exception.NotFoundException;
import user.management.mapper.UserMapper;
import user.management.model.request.CreateUserRequest;
import user.management.model.request.UpdateUserRequest;
import user.management.model.response.UserResponse;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        isEmailExists(request.getEmail());
        UserEntity entity = mapper.toEntity(request);
        entity.setStatus(UserStatus.ACTIVATE);
        return mapper.toResponse(userRepository.save(entity));
    }

    public List<UserResponse> getAllUsers() {
        return mapper.toResponse(userRepository.findAll());
    }

    public UserResponse getUserById(Long id) {
        return mapper.toResponse(getUserEntityById(id));
    }

    public UserResponse getByEmail(String email) {
        return mapper.toResponse(userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found")));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        UserEntity oldEntity = getUserEntityById(id);
        UserEntity newEntity = mapper.toEntity(oldEntity, request);
        return mapper.toResponse(userRepository.save(newEntity));
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity entity = getUserEntityById(id);
        entity.setStatus(UserStatus.DELETED);
        userRepository.save(entity);
    }

    private void isEmailExists(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("User Already exists");
        }
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
    }
}
