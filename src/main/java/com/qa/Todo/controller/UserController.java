package com.qa.Todo.controller;

import com.qa.Todo.dto.UserDTO;
import com.qa.Todo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin

@RequestMapping("/users")
public class UserController {
    private UserService service;
    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }
    //create
    @PostMapping("/create")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO){
        System.out.println(userDTO.getClass());
        UserDTO created = this.service.createUser(userDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    //readAll
    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> read(){
        return ResponseEntity.ok(this.service.readAllUsers());
    }
    //readById
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDTO> readByID(@PathVariable Long userId){
        return ResponseEntity.ok(this.service.read(userId));
    }
    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO,@PathVariable Long userId){
        System.out.println(userId);
        System.out.println(userDTO.getClass());
        UserDTO updated = this.service.update(userDTO,userId);
        return new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
    }
    //delete
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserDTO> delete(@PathVariable Long userId){
        return this.service.delete(userId)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

