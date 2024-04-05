package com.fmi.eduhub.service;

import com.fmi.eduhub.dto.UserModel;
import com.fmi.eduhub.dto.input.UserProfileInput;
import com.fmi.eduhub.dto.output.UserProfileOutput;
import com.fmi.eduhub.dto.output.UserSimpleOutput;
import com.fmi.eduhub.entity.CourseEntity;
import com.fmi.eduhub.entity.UserEntity;
import com.fmi.eduhub.enums.UserRoleEnum;
import com.fmi.eduhub.exception.ExceptionConstants;
import com.fmi.eduhub.exception.ResourceNotAccessibleException;
import com.fmi.eduhub.exception.ResourceNotFoundException;
import com.fmi.eduhub.mapper.UserEntityMapper;
import com.fmi.eduhub.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersEntityService implements UserDetailsService {
    private final UserEntityRepository userEntityRepository;

    private final FileUploadService fileUploadService;

    private final UserEntityMapper userEntityMapper = UserEntityMapper.INSTANCE;

    /*
    public UserEntity insertDataH2() {
        String userId = "00634ece-8efa-4e32-8e8f-faf41b96d833";
        Optional<UserEntity> dbUser = userEntityRepository.findById(UUID.fromString(userId));
        if(dbUser.isPresent()) {
            return dbUser.get();
        }
        UserEntity user = new UserEntity();
        user.setUserId(UUID.fromString(userId));
        user.setFirstName("Francesco");
        user.setLastName("Testare");
        user.setEmail("francesco.testare@yahoo.com");
        user.setPassword("Parola1234");
        user.setUserType("STANDARD");
        user = userEntityRepository.save(user);
        return user;
    }
     */

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserEntity> dbUserEntity = userEntityRepository.findByEmail(email);
        return dbUserEntity.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userEntityRepository.findByEmail(username);
        if(userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return new
                org.springframework.security.core.userdetails.User(userEntity.getUsername(), userEntity.getPassword(), userEntity.getAuthorities());
        } else {
            throw new UsernameNotFoundException("Account doesn't exist!");
        }
    }

    public ResponseEntity<UserModel> findUserById(String userId) {
       //System.out.println("In userEntityService");
        Optional<UserEntity> dbUser =
                userEntityRepository.findById(UUID.fromString(userId));
        System.out.println(dbUser);
        if(dbUser.isPresent()) {
            UserModel userModel = userEntityMapper.fromUserEntityToModel(dbUser.get());
            return new ResponseEntity<>(userModel, HttpStatus.FOUND);
        } else {
            throw new UsernameNotFoundException(ExceptionConstants.USERNAME_NOT_FOUND);
        }
    }

    @Transactional
    public boolean deleteUser(String userId) {
        UUID userIdToDelete = UUID.fromString(userId);
        UserEntity currentUser = this.getCurrentUser();

        if(currentUser.getUserType() != UserRoleEnum.ADMIN
            || currentUser.getUserId() == userIdToDelete) {
            throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
        }

        UserEntity entityToDelete = findByUserId(userIdToDelete);
        if (entityToDelete.getUserType() == UserRoleEnum.ADMIN) {
            throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
        }
        fileUploadService.deleteFile(entityToDelete.getProfilePictureKey());
        userEntityRepository.delete(entityToDelete);
        return true;
    }

    public UserEntity findByUserId(UUID userId) {
        Optional<UserEntity> dbUser = userEntityRepository.findById(userId);
        if(dbUser.isEmpty()) {
            throw new ResourceNotFoundException(ExceptionConstants.USERNAME_NOT_FOUND);
        }
        return dbUser.get();
    }

    public boolean updateProfilePicture(MultipartFile file) {
        // TO DO: validation of image
        UserEntity user = getCurrentUser();
        if(file != null && user != null) {
            String s3Key = fileUploadService.uploadFile(file);
            user.setProfilePictureKey(s3Key);
            userEntityRepository.save(user);
            return true;
        }
        return false;
    }

    public UserProfileOutput getUserProfile() {
        UserEntity user = this.getCurrentUser();
        if(user != null) {
            return fromUserEntityToProfileOutput(user);
        }
        throw new ResourceNotAccessibleException(ExceptionConstants.PROFILE_NOT_ACCESSIBLE);
    }

    @Transactional
    public boolean updateUserProfile(UserProfileInput userProfileInput) {
        UserEntity user = this.getCurrentUser();
        UserEntity userToUpdate = this.findByUserId(UUID.fromString(userProfileInput.getUserId()));
        UserEntity entityToUpdateFrom = userEntityMapper.fromUserProfileInputToEntity(userProfileInput);
        if(user != null){
            if (user.getUserType() == UserRoleEnum.ADMIN) {
                if(Objects.equals(userProfileInput.getUserId(), user.getUserId().toString())) {
                    userToUpdate = updateUserEntity(userToUpdate, entityToUpdateFrom, false);
                } else {
                    userToUpdate = updateUserEntity(user, entityToUpdateFrom, true);
                }
            } else {
                if(!Objects.equals(userToUpdate.getUserId(), entityToUpdateFrom.getUserId())) {
                    throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
                }
                userToUpdate = updateUserEntity(userToUpdate, entityToUpdateFrom, false);
            }

            if(userProfileInput.getProfileImage() != null) {
                fileUploadService.deleteFile(userToUpdate.getProfilePictureKey());
                String profilePictureKey = fileUploadService.uploadFile(userProfileInput.getProfileImage());
                userToUpdate.setProfilePictureKey(profilePictureKey);
            }
            return true;
        }
        throw new ResourceNotAccessibleException(ExceptionConstants.ACCESS_DENIED);
    }

    public UserEntity updateUserEntity(UserEntity dbEntity, UserEntity entityToUpdateFrom, boolean isAdmin) {
        dbEntity.setEmail(entityToUpdateFrom.getEmail());
        dbEntity.setFirstName(entityToUpdateFrom.getFirstName());
        dbEntity.setLastName(entityToUpdateFrom.getLastName());
        if(isAdmin) {
            dbEntity.setUserType(entityToUpdateFrom.getUserType());
        }
        return dbEntity;
    }

    public UserSimpleOutput entityToOutput(UserEntity userEntity) {
        UserSimpleOutput userSimpleOutput =
            userEntityMapper.fromEntityToOutput(userEntity);
        if(userEntity.getProfilePictureKey() != null) {
            userSimpleOutput.setProfilePictureUrl(
                fileUploadService.generatePreSignedUrl(userEntity.getProfilePictureKey()));
        }
        return userSimpleOutput;
    }

    public UserProfileOutput fromUserEntityToProfileOutput(UserEntity userEntity) {
        UserEntity authenticatedUser = this.getCurrentUser();
        UserProfileOutput userProfileOutput =
            userEntityMapper.fromEntityToProfileOutput(userEntity);
        if(authenticatedUser.getUserType() == UserRoleEnum.ADMIN) {
            if(authenticatedUser.getUserId() != userEntity.getUserId()) {
                userProfileOutput.setCanEditAdmin(true);
                userProfileOutput.setCanEditUser(false);
            } else {
                userProfileOutput.setCanEditAdmin(false);
                userProfileOutput.setCanEditUser(true);
            }
        }
        if(authenticatedUser.getUserType() == UserRoleEnum.AUTHOR
            || authenticatedUser.getUserType() == UserRoleEnum.USER) {
            userProfileOutput.setCanEditUser(true);
            userProfileOutput.setCanEditAdmin(false);
        }

        userProfileOutput.setProfilePictureUrl(getProfileUrl(userEntity));
        return userProfileOutput;
    }

    public String getProfileUrl(UserEntity userEntity) {
        if(userEntity.getProfilePictureKey() != null) {
            return fileUploadService.generatePreSignedUrl(userEntity.getProfilePictureKey());
        }
        return null;
    }

    public boolean isAuthorOfCourse(UserEntity user, CourseEntity courseEntity) {
        return user.getUserId() == courseEntity.getAuthor().getUserId();
    }
}
