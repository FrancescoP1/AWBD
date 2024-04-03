package com.fmi.eduhub.mapper;

import com.fmi.eduhub.dto.UserModel;
import com.fmi.eduhub.dto.input.UserProfileInput;
import com.fmi.eduhub.dto.input.UserRegistrationModel;
import com.fmi.eduhub.dto.output.UserProfileOutput;
import com.fmi.eduhub.dto.output.UserSimpleOutput;
import com.fmi.eduhub.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {

    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "emailAddress", target = "email")
    UserEntity fromUserRegistrationModelToEntity(UserRegistrationModel registrationModel);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userType", target = "userType")
    UserModel fromUserEntityToModel(UserEntity entity);

    UserSimpleOutput fromEntityToOutput(UserEntity userEntity);

    UserProfileOutput fromEntityToProfileOutput(UserEntity userEntity);

    UserEntity fromUserProfileInputToEntity(UserProfileInput userProfileInput);
}
