package com.fastcampus.board.repository;

import com.fastcampus.board.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserDTO, Integer> {
    boolean existsUserDTOByUserId(String userId);
    Optional<UserDTO> findUserDtoByUserIdAndPassword(String userId, String password);
    Optional<UserDTO> findUserDtoByUserId(String userId);
}
