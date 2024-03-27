/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.service;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.common.AuthorizationRole;
import org.example.common.LoadingCache;
import org.example.contract.AuthContract;
import org.example.contract.UserContract;
import org.example.dto.*;
import org.example.entity.Log;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.mapper.UserMapper;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.repository.UserRoleRepository;
import org.example.state.LoginState;
import org.example.state.UserState;
import org.mindrot.jbcrypt.BCrypt;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Singleton
public class UserService implements UserContract, AuthContract {

    @NonNull private ConnectionSource connectionSource;

    @NonNull private UserRepository userRepository;

    @NonNull private RoleRepository roleRepository;

    @NonNull private UserRoleRepository userRoleRepository;

    @NonNull private LogService logService;

    @NonNull private LoadingCache<UUID, User> userCache;

    private final LoadingCache.CacheLoader<UUID, User> userCacheLoader = new LoadingCache.CacheLoader<>() {
        @Override
        public User load(UUID key) throws SQLException {
            User user = userRepository.read(key);
            if (user == null) {
                log.debug("User not found: {}", key);
                return null;
            }

            log.debug("get user from db: {}", user);
            return user;
        }
    };

    @Override
    public LoginState validateUserCredentials(LoginDTO dto) throws SQLException {
        // TODO: 1/4/24 implement login by email
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);

        if (user == null) {
            return LoginState.builder().state(LoginState.State.USER_NOT_FOUND).build();
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            // log
            // TODO: 12/30/23 need to user transaction or not?

            logService.makeLog(Log.Type.INVALID_LOGIN, Pair.of("username", dto.getUsername()));
            return LoginState.builder()
                    .state(LoginState.State.INVALID_CREDENTIALS)
                    .build();
        }

        logService.makeLog(Log.Type.LOGIN, Pair.of("username", dto.getUsername()));
        return LoginState.builder()
                .state(LoginState.State.SUCCESS)
                .uuid(user.getId())
                .build();
    }

    @Override
    public void assignRoleToUser(UUID uuid, AuthorizationRole role) throws SQLException {
        User user = getUser(uuid);

        if (user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals(role.getRole()))) {
            log.error("User {} already has role {}", user.getUsername(), role.getRole());
            return;
        }

        UserRole userRole = new UserRole(user, roleRepository.getByName(role.getRole()));
        userRoleRepository.create(userRole);

        logService.makeLog(
                Log.Type.ROLE_CHANGED,
                Pair.of("type", "added"),
                Pair.of("role", role.getRole()),
                Pair.of("username", user.getUsername()));
        log.info("Added role {} to user {}", role.getRole(), user.getUsername());

        // refresh cache
        userCache.remove(uuid);
    }

    @Override
    public void revokeRoleFromUser(UUID uuid, AuthorizationRole role) throws SQLException {
        User user = getUser(uuid);

        Optional<UserRole> userRole1 = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRole().getName().equals(role.getRole()))
                .findFirst();
        if (userRole1.isEmpty()) {
            log.error("User {} not has role {}", user.getUsername(), role.getRole());
            return;
        }

        userRoleRepository.delete(userRole1.get());

        logService.makeLog(Log.Type.ROLE_CHANGED, Pair.of("type", "removed"), Pair.of("role", role.getRole()));
        log.info("Removed role {} from user {}", role.getRole(), user.getUsername());

        userCache.remove(uuid);
    }

    @Override
    public boolean doesUserHaveRole(UUID uuid, AuthorizationRole role) throws SQLException {
        User user = getUser(uuid);
        if (user == null) {
            return false;
        }

        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals(role.getRole()));
    }

    @Override
    public DataTableDTO<UserDTO> fetchUsersForDataTable(DataTableRequestDTO dto) throws SQLException {
        return TransactionManager.callInTransaction(connectionSource, () -> {
            DataTableDTO<UserDTO> dataTableDTO = new DataTableDTO<>();
            dataTableDTO.setRecordsTotal(userRepository.count());
            dataTableDTO.setDraw(dto.getDraw());
            List<User> users = userRepository.queryForDataTable(
                    dto.getStart(),
                    dto.getLength(),
                    dto.getColumns(),
                    dto.getSearch().getValue(),
                    dto.getOrder().get(0).getColumn(),
                    dto.getOrder().get(0).getDir());
            dataTableDTO.setRecordsFiltered(userRepository.countForDataTable(
                    dto.getColumns(), dto.getSearch().getValue()));

            dataTableDTO.setData(
                    users.stream().map(UserMapper.INSTANCE::mapFromEntity).collect(Collectors.toList()));

            return dataTableDTO;
        });
    }

    @Override
    public UserState deleteUser(UUID uuid) throws SQLException {
        return TransactionManager.callInTransaction(connectionSource, () -> {
            userRoleRepository.deleteByUserId(uuid);
            userRepository.deleteById(uuid);

            userCache.remove(uuid);
            return UserState.builder().state(UserState.State.SUCCESS).build();
        });
    }

    @Override
    public UserState updateUser(UUID uuid, UpdateUserDTO dto) throws SQLException {
        User user = getUser(uuid);

        List<AuthorizationRole> existingRoles = user.getUserRoles().stream()
                .map(userRole -> AuthorizationRole.from(userRole.getRole().getName()))
                .collect(Collectors.toList());

        return TransactionManager.callInTransaction(connectionSource, () -> {
            // check if the username exists
            if (StringUtils.isNotEmpty(dto.getUsername())
                    && !StringUtils.equals(user.getUsername(), dto.getUsername())
                    && userRepository.findByUsername(dto.getUsername()).isPresent()) {
                return UserState.builder().state(UserState.State.USERNAME_TAKEN).build();
            }

            // check if email exists
            if (StringUtils.isNotEmpty(dto.getEmail())
                    && !StringUtils.equals(user.getEmail(), dto.getEmail())
                    && userRepository.findByEmail(dto.getEmail()).isPresent()) {
                return UserState.builder().state(UserState.State.EMAIL_TAKEN).build();
            }

            if (dto.getRoles() != null) {
                List<AuthorizationRole> newRoles = new ArrayList<>();
                for (String role : dto.getRoles()) {
                    AuthorizationRole role1 = AuthorizationRole.from(role);
                    if (role1 == null) {
                        return UserState.builder()
                                .state(UserState.State.INVALID_ROLE)
                                .build();
                    }
                    newRoles.add(role1);
                }

                // roles to be deleted
                for (AuthorizationRole authorizationRole : existingRoles.stream()
                        .filter(authorizationRole -> !newRoles.contains(authorizationRole))
                        .collect(Collectors.toList())) {
                    revokeRoleFromUser(uuid, authorizationRole);
                }

                // roles to be added
                for (AuthorizationRole newRole : newRoles) {
                    assignRoleToUser(uuid, newRole);
                }
            }

            UserMapper.INSTANCE.updateFromDTO(user, dto);

            // hash new password
            if (StringUtils.isNotBlank(dto.getNewPassword())) {
                String hashedPassword = BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt(10, new SecureRandom()));
                user.setPassword(hashedPassword);
            }

            userRepository.update(user);
            userCache.remove(uuid);

            return UserState.builder().state(UserState.State.SUCCESS).build();
        });
    }

    @Override
    public UserState addNewUser(UserDTO dto) throws SQLException {
        User user = UserMapper.INSTANCE.mapFromDTO(dto);

        return TransactionManager.callInTransaction(connectionSource, () -> {
            // check if the username exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return UserState.builder().state(UserState.State.USERNAME_TAKEN).build();
            }

            // check if email exists
            if (StringUtils.isNotEmpty(user.getEmail())
                    && userRepository.findByEmail(user.getEmail()).isPresent()) {
                return UserState.builder().state(UserState.State.EMAIL_TAKEN).build();
            }

            // hashing password
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10, new SecureRandom()));
            user.setPassword(hashedPassword);

            log.info("Creating new user: {}", user);
            UUID uuid = userRepository.create(user);
            user.setId(uuid);

            // log of creating new user
            logService.makeLog(Log.Type.NEW_USER, Pair.of("username", user.getUsername()));

            // assign role user
            assignRoleToUser(uuid, AuthorizationRole.ROLE_USER);

            // the first user is admin
            if (userRepository.count() == 1) {
                assignRoleToUser(uuid, AuthorizationRole.ROLE_ADMIN);
            }

            return UserState.builder().uuid(uuid).state(UserState.State.SUCCESS).build();
        });
    }

    @Override
    public User getUser(UUID uuid) throws SQLException {
        return userCache.get(uuid, userCacheLoader);
    }
}
