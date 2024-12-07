package com.yoongu.security.persistence.auth;

import com.yoongu.security.apiserver.auth.dto.UserSearchCondition;
import com.yoongu.security.apiserver.auth.dto.request.UserCommonRequest;
import com.yoongu.security.apiserver.auth.dto.request.UserModificationRequest;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDataService {

    private final UserRepository userRepository;

    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void doLoginProcess(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.login();
    }

    public void lockUserAccount(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.setAccountNonLocked(false);
    }

    public void increaseLoginFailureCount(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.increaseLoginFailureCount();
    }

    public Page<User> getUsers(Pageable pageable, UserSearchCondition userSearchCondition) {
        return userRepository.findByCondition(pageable, userSearchCondition);
    }

    public void updateUser(UserModificationRequest modificationRequest) {
        String userName = modificationRequest.getUserName();
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.updateInfo(modificationRequest);
    }

    public void deleteUser(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        userRepository.delete(user);
    }

    public void resetUser(UserCommonRequest commonRequest) {
        String userName = commonRequest.getUserName();
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.reset(commonRequest.getPassword());
    }

    public void changePassword(UserCommonRequest commonRequest) {
        String userName = commonRequest.getUserName();
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(userName + "is not exists!"));
        user.changePassword(commonRequest.getPassword());
    }

}
