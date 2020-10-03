package com.qa.Todo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.Todo.dto.TaskDTO;
import com.qa.Todo.presistence.domain.Tasks;
import com.qa.Todo.presistence.domain.Users;
import com.qa.Todo.presistence.repo.TaskRepo;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {
    // autowiring objects for mocking different aspects of the application
    // here, a mock repo (and relevant mappers) are autowired
    // they'll 'just work', so we don't need to worry about them
    // all we're testing is how our controller integrates with the rest of the API

    // mockito's request-making backend
    // you only need this in integration testing - no mocked service required!
    // this acts as postman would, across your whole application
    @Autowired
    private MockMvc mock;

    // i'm reusing my normal repo to ping different things to for testing purposes
    // this is only used for my <expected> objects, not <actual> ones!
    @Autowired
    private TaskRepo repo;

    // this specifically maps POJOs for us, in our case to JSON
    // slightly different from ObjectMapper because we built it ourselves (and use
    // it exclusively on our <expected> objects
    @Autowired
    private ModelMapper modelMapper;

    // this specifically maps objects to JSON format for us
    // slightly different from ModelMapper because this is bundled with mockito
    @Autowired
    private ObjectMapper objectMapper;

    private Tasks testTasks;
    private Tasks testTasksWithId;
    private TaskDTO taskDTO;

    private Long id;

    private TaskDTO mapToDTO(Tasks task) {
        return this.modelMapper.map(task, TaskDTO.class);
    }

    @BeforeEach
    void init() {
        this.repo.deleteAll();

        this.testTasks = new Tasks("My Task","Hello World",
                new Date(), new Date(), new Users(
                1L,"Joni","Baki","mjoni",
                "mjoni@qa.com","123456"));
        this.testTasksWithId = this.repo.save(this.testTasks);
        this.taskDTO = this.mapToDTO(this.testTasksWithId);
        this.id = this.testTasksWithId.getTaskId();
    }

    @Test
    void testCreate() throws Exception {
        this.mock
                .perform(request(HttpMethod.POST, "/task/create").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(this.testTasks))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(this.objectMapper.writeValueAsString(this.taskDTO)));
    }

    @Test
    void testRead() throws Exception {
        this.mock.perform(request(HttpMethod.GET, "/task/read/" + this.id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(this.objectMapper.writeValueAsString(this.taskDTO)));
    }

    @Test
    void testReadAll() throws Exception {
        List<TaskDTO> taskList = new ArrayList<>();
        taskList.add(this.taskDTO);
        String expected = this.objectMapper.writeValueAsString(taskList);
        // expected = { { "name": "Nick", ... } , { "name": "Cris", ... } }

        String actual = this.mock.perform(request(HttpMethod.GET, "/task/read").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(expected, actual);
    }

//    @Test
//    void testUpdate() throws Exception {
//        TasksDTO newTasks = new TasksDTO(null, "Peter Peter Hughes", 4, "Fender American");
//        Tasks updatedTasks = new Tasks(newTasks.getName(), newTasks.getStrings(),
//                newTasks.getType());
//        updatedTasks.setId(this.id);
//        String expected = this.objectMapper.writeValueAsString(this.mapToDTO(updatedTasks));
//
//        String actual = this.mock.perform(request(HttpMethod.PUT, "/task/update/" + this.id) // localhost:8901/task/update/1
//                .contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(newTasks))
//                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted()) // 201
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals(expected, actual);
//    }

    @Test
    void testDelete() throws Exception {
        this.mock.perform(request(HttpMethod.DELETE, "/task/delete/" + this.id)).andExpect(status().isNoContent());
    }
}
