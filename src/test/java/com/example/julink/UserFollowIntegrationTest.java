package com.example.julink;

import com.example.julink.bulk.entity.College;
import com.example.julink.bulk.repositories.CollegeRepository;
import com.example.julink.entryrelated.entity.Users;
import com.example.julink.entryrelated.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BulkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CollegeRepository collegeRepo;

    private Users user1;
    private Users user2;
    private Long collegeId;

    @BeforeEach
    void setUp() {
        College college = new College();
        college.setName("Test College");
        college = collegeRepo.save(college);
        collegeId = college.getId();

        user1 = new Users();
        user1.setUsername("ibrahim");
        user1.setEmail("ibrahim@test.com");
        user1.setPassword("secret");
        user1.setFirstName("Ibrahim");
        user1.setLastName("Tester");
        user1.setActive(true);
        user1.setRole("USER");
        user1.setCollege(college);
        user1 = userRepo.save(user1);

        user2 = new Users();
        user2.setUsername("ahmed");
        user2.setEmail("ahmed@test.com");
        user2.setPassword("secret");
        user2.setFirstName("Ahmed");
        user2.setLastName("Tester");
        user2.setActive(true);
        user2.setRole("USER");
        user2.setCollege(college);
        user2 = userRepo.save(user2);
    }

    // --- FOLLOW TESTS ---
    @Test
    @WithMockUser(username = "ibrahim", roles = {"USER"})
    void testFollowUser() throws Exception {
        mockMvc.perform(post("/api/" + user2.getId() + "/follow")
                        .param("followerId", String.valueOf(user1.getId())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ibrahim", roles = {"USER"})
    void testUnfollowUser() throws Exception {
        // First follow
        mockMvc.perform(post("/api/" + user2.getId() + "/follow")
                .param("followerId", String.valueOf(user1.getId())));

        // Then unfollow
        mockMvc.perform(delete("/api/" + user2.getId() + "/follow")
                        .param("followerId", String.valueOf(user1.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "ibrahim", roles = {"USER"})
    void testGetFollowingAndFollowers() throws Exception {
        mockMvc.perform(post("/api/" + user2.getId() + "/follow")
                        .param("followerId", String.valueOf(user1.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/" + user1.getId() + "/following"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ahmed"));

        mockMvc.perform(get("/api/" + user2.getId() + "/followers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ibrahim"));
    }

    // --- PROFILE TESTS ---
    @Test
    @WithMockUser(username = "ibrahim", roles = {"USER"})
    void testGetProfile() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ibrahim"));
    }

    // You can add more for updateProfile, deleteProfileImage, deactivateAccount etc.

    // --- POSTS TESTS ---
    // These will test creating, editing, deleting posts, upload images, like/unlike posts

    // --- COMMENTS TESTS ---
    // These will test create, edit, delete comments, get comments by post

}
