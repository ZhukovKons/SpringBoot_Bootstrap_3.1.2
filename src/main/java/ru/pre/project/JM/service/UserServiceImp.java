package ru.pre.project.JM.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pre.project.JM.models.Role;
import ru.pre.project.JM.models.RoleType;
import ru.pre.project.JM.models.User;
import ru.pre.project.JM.repositorys.RoleRepository;
import ru.pre.project.JM.repositorys.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public void add(User user) {
        userRepository.save(user);
    }

    @Override
    public void edit(User user, long id) { // todo странный метод
        user.setRoles(getUser(id).getRoles());
        Role addRole = roleRepository.findAll().stream()
                .filter(x -> user.getAddRole().equals(x.getRole().name()))
                .findAny().orElse(null);
        if (addRole != null){
            user.getRoles().add(addRole);
        }
        user.setRoles(user.getRoles().stream().filter(x -> !x.getRole().name().equals(user.getDeleteRole())).collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void remove(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(s);
    }

    @Override
    public void addDefaultRoles() {
        roleRepository.save(new Role(RoleType.ADMIN));
        roleRepository.save(new Role(RoleType.USER));
    }
}
