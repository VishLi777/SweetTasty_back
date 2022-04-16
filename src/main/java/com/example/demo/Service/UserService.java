package com.example.demo.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.demo.Controller.AuxiliaryClasses.StaticMethods;
import com.example.demo.DTO.UserDTO;
import com.example.demo.Entity.Enum.ERoles;
import com.example.demo.Entity.Response.ResponseClass;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.Service.JWTokenService;
import com.example.demo.Singleton.SingletonOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Security.SecurityConstants.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleService roleService;
    @Autowired
    ValidationService validationService;
    @Autowired
    SendingSMS sendingSMS;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JWTokenService jwTokenService;

    /**
     * Добавление пользователя
     * @param body [json] поля сущности UserEntity
     * @code 201 - Created
     * @code 400 - Incorrect JSON
     * @code 432 - User with this telephoneNumber already exist
     * @code 469 - Incorrect validation (ValidationService.class)
     */
    public void addUser(String body, HttpServletRequest request, HttpServletResponse response) {

        String fio = StaticMethods.parsingJson(body, "FIO", request, response);
        String password = StaticMethods.parsingJson(body, "password", request, response);
        String telephoneNumber = StaticMethods.parsingJson(body, "telephoneNumber", request, response);

        if(fio == null || password == null || telephoneNumber == null)
            return;

        UserEntity userEntity = findByTelephoneNumber(telephoneNumber);
        if(userEntity != null) {
            StaticMethods.createResponse(request, response,
                    432,"User with this telephoneNumber already exist");
            return;
        }

        userEntity = new UserEntity();

        List<Role> role = new ArrayList<>();
        role.add(roleService.findByRole(ERoles.valueOf("USER")));
        userEntity.setTelephoneNumber(telephoneNumber);
        userEntity.setRoles(role);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setFIO(fio);

        userEntity.setIsMan(null);
        userRepository.save(userEntity);

        StaticMethods.createResponse(request, response, HttpServletResponse.SC_CREATED, "Created");
    }


    /**
     * Удаление пользователя по полю :telephoneNumber
     * @param telephoneNumber :telephoneNumber Пользователя
     */
    public void deleteByTelephoneNumber(String telephoneNumber) {
        UserEntity userEntity = findByTelephoneNumber(telephoneNumber);
        userRepository.delete(userEntity);
    }


    /**
     * Поиск Пользователя по полю :telephoneNumber
     * @param telephoneNumber :telephoneNumber Пользователя
     */
    public UserEntity findByTelephoneNumber(String telephoneNumber){
        return userRepository.findByTelephoneNumber(telephoneNumber);
    }


    /**
     * Проверка роли пользователя
     * @param request request, в котором должен быть jwToken
     * @code 200 - ResponseEntity<ResponseClass>
     * @code 400 - Incorrect JWT token
     */
    public ResponseEntity<ResponseClass> checkRole(HttpServletRequest request) {

        String tokenWithPrefix = request.getHeader(HEADER_JWT_STRING);
        if(tokenWithPrefix!=null && tokenWithPrefix.startsWith(TOKEN_PREFIX)){
            String token = tokenWithPrefix.replace(TOKEN_PREFIX,"");
            try{
                String role = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token)
                        .getClaim("role").asString();

                return ResponseEntity.ok(new ResponseClass(200, role, request.getServletPath()));
            }catch (IllegalArgumentException | JWTDecodeException ignored){}

        }


        return ResponseEntity.status(400).body(new ResponseClass(400, "Incorrect JWT token", request.getServletPath()));

    }

    public void changePassword(String body, HttpServletRequest request, HttpServletResponse response) {

        String telephoneNumber = StaticMethods.parsingJson(body, "telephoneNumber", request, response);
        String newPassword = StaticMethods.parsingJson(body, "password", request ,response);
        if(newPassword == null || telephoneNumber == null)
            return;

        UserEntity userEntity = findByTelephoneNumber(telephoneNumber);
        if(userEntity == null){
            StaticMethods.createResponse(request, response, 432, "User with this :telephoneNumber doesn't exist");
            return;
        }

        userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        StaticMethods.createResponse(request, response, 201, "Password have changed");

    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public void delete(UserEntity userEntity){
        userRepository.delete(userEntity);
    }

    public UserDTO getInfoAboutUser(HttpServletRequest request) {
        String telephoneNumber = jwTokenService.
                getNameFromJWT(request.getHeader(HEADER_JWT_STRING).replace(TOKEN_PREFIX, ""));
        UserEntity userEntity = userRepository.findByTelephoneNumber(telephoneNumber);

        return UserDTO.createUserDTO(userEntity);
    }
}
