package com.fastcampus.board.service.impl;

import com.fastcampus.board.dto.UserDTO;
import com.fastcampus.board.exception.DuplicateIdException;
import com.fastcampus.board.repository.UserRepository;
import com.fastcampus.board.service.UserService;
import com.fastcampus.board.util.SHA256Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(UserDTO userProfile) {
        boolean duplIdResult = isDuplicatedId(userProfile.getUserId());
        if (duplIdResult) {
            throw new DuplicateIdException("중복된 아이디입니다.");
        }

        userProfile.setCreateTime(new Date());
        userProfile.setPassword(SHA256Util.encryptSHA256(userProfile.getPassword()));

        try {
            userRepository.save(userProfile);
        }
        catch (Exception ex) {
            log.error("insertMember ERROR! {}", userProfile);
            throw new RuntimeException(
                    "insertUser ERROR! 회원가입 메서드를 확인해주세요\n" + "Params : " + userProfile);
        }
    }

    @Override
    public UserDTO login(String id, String password) {
        String cryptoPassword = SHA256Util.encryptSHA256(password);
        Optional<UserDTO> memberInfo = userRepository.findUserDtoByUserIdAndPassword(id, cryptoPassword);
        return memberInfo.get();
    }

    @Override
    public boolean isDuplicatedId(String id) {
        return userRepository.existsUserDTOByUserId(id);
    }

    @Override
    public UserDTO getUserInfo(String userId) {
        return userRepository.findUserDtoByUserId(userId).get();
    }

    @Override
    public void updatePassword(String id,
                               String beforePassword,
                               String afterPassword) {
        String cryptoPassword = SHA256Util.encryptSHA256(beforePassword);
        Optional<UserDTO> memberInfo = userRepository.findUserDtoByUserIdAndPassword(id, cryptoPassword);

        if (memberInfo != null) {
            memberInfo.get().setPassword(SHA256Util.encryptSHA256(afterPassword));
            userRepository.save(memberInfo.get());
        } else {
            log.error("updatePasswrod ERROR! {}", memberInfo);
            throw new IllegalArgumentException("updatePasswrod ERROR! 비밀번호 변경 메서드를 확인해주세요\n" + "Params : " + memberInfo);
        }
    }

    @Override
    public void deleteId(String id, String password) {
        String cryptoPassword = SHA256Util.encryptSHA256(password);
        Optional<UserDTO> memberInfo = userRepository.findUserDtoByUserIdAndPassword(id, cryptoPassword);

        if (memberInfo != null) {
            userRepository.delete(memberInfo.get());
        } else {
            log.error("deleteId ERROR! {}", memberInfo);
            throw new RuntimeException("deleteId ERROR! id 삭제 메서드를 확인해주세요\n" + "Params : " + memberInfo);
        }
    }
}
