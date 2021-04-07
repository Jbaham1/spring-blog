package com.codeup.springblog;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repo.PostRepo;
import com.codeup.springblog.repo.UserRepo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Collections;

import static org.springframework.http.RequestEntity.*;
import static org.springframework.http.RequestEntity.post;


@RunWith(SpringRunner.class)@SpringBootTest(classes = SpringBlogApplication.class)
@AutoConfigureMockMvc
public class PostsIntegrationTests {

    private User testUser;
    private HttpSession httpSession;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepo userDao;

    @Autowired
    PostRepo postDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {

        User testUser = userDao.findByUsername("testUser");

        // Creates the test user if not exists
        if (testUser == null) {
            User newUser = new User();
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("pass"));
            newUser.setEmail("testUser@codeup.com");
            testUser = userDao.save(newUser);
        }

        // Throws a Post request to /login and expect a redirection to the Ads index page after being logged in
        httpSession = this.mvc.perform(post(URI.create("/login")).with(SecurityMockServerConfigurers.csrf())
                .param("username", "testUser")
                .param("password", "pass"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FOUND.value()))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void contextLoads() {
        // Sanity Test, just to make sure the MVC bean is working
        Assert.assertNotNull(mvc);
    }

    @Test
    public void testIfUserSessionIsActive() throws Exception {
        // It makes sure the returned session is not null
        Assert.assertNotNull(httpSession);
    }

    @Test
    public void testCreateAd() throws Exception {
        // Makes a Post request to /ads/create and expect a redirection to the Ad
        this.mvc.perform(
                post(URI.create("/posts/create")).with(SecurityMockMvcRequestPostProcessors.csrf())
                        .session((MockHttpSession) httpSession)
                        // Add all the required parameters to your request like this
                        .param("title", "test")
                        .param("body", "for sale"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void testShowAd() throws Exception {

        Post existingPost = postDao.findAll().get(0);
        System.out.println("existingAd.getTitle() = " + existingPost.getTitle());

        // Makes a Get request to /ads/{id} and expect a redirection to the Ad show page
        this.mvc.perform(get("/posts/" + existingPost.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Test the dynamic content of the page
                .andExpect(MockRestRequestMatchers.content().string(StringContains.containsString(existingPost.getTitle())));
    }

    @Test
    public void testAdsIndex() throws Exception {
        Post existingAd = postDao.findAll().get(0);

        // Makes a Get request to /ads and verifies that we get some of the static text of the ads/index.html template and at least the title from the first Ad is present in the template.
        this.mvc.perform(Collections.get("/posts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Test the static content of the page
                .andExpect(MockRestRequestMatchers.content().string(CoreMatchers.containsString("Here's a list of all posts")))
                // Test the dynamic content of the page
                .andExpect(MockRestRequestMatchers.content().string(CoreMatchers.containsString(existingAd.getTitle())));
    }

    @Test
    public void testEditAd() throws Exception {
        // Gets the first Ad for tests purposes
        Post existingPost = (Post) postDao.findPostsByTitle("test").get(0);

        // Makes a Post request to /ads/{id}/edit and expect a redirection to the Ad show page
        this.mvc.perform(
                post("/posts/" + existingPost.getId() + "/edit").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .session((MockHttpSession) httpSession)
                        .param("title", "edited title")
                        .param("body", "edited body"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // Makes a GET request to /ads/{id} and expect a redirection to the Ad show page
        this.mvc.perform(Collections.get("/posts/" + existingPost.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Test the dynamic content of the page
                .andExpect(MockRestRequestMatchers.content().string(CoreMatchers.containsString("edited title")))
                .andExpect(MockRestRequestMatchers.content().string(CoreMatchers.containsString("edited body")));
    }

    @Test
    public void testDeleteAd() throws Exception {
        // Creates a test Ad to be deleted
        this.mvc.perform(
                post("/posts/create").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .session((MockHttpSession) httpSession)
                        .param("title", "ad to be deleted")
                        .param("body", "won't last long"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        // Get the recent Ad that matches the title
        Post existingPost = postDao.findPostsByTitle("post to be deleted").get(0);
        System.out.println("existingAd.getTitle() = " + existingPost.getTitle());
        // Makes a Post request to /ads/{id}/delete and expect a redirection to the Ads index
        this.mvc.perform(
                post("/posts/delete").with(csrf())
                        .session((MockHttpSession) httpSession)
                        .param("id", String.valueOf(existingPost.getId())))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}

