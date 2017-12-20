package com.Controller;

import com.bootcamp.application.Application;
import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.commons.ws.usecases.pivotone.LikeWS;
import com.bootcamp.controllers.LikeController;
import com.bootcamp.entities.LikeTable;
import com.bootcamp.services.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
/**
 * Created by Archange on 17/12/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = LikeController.class, secure = false)
@ContextConfiguration(classes={Application.class})
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LikeService likeService;


    @Test
    public void createLike() throws Exception{
        List<LikeTable> likeTables =  getLikeTableFromJson();
        LikeTable likeTable = likeTables.get(0);

        when(likeService.read(0)).thenReturn(likeTable);
        when(likeService.create(likeTable)).thenReturn(likeTable);
        RequestBuilder requestBuilder =
                post("/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(likeTable));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        mockMvc.perform(requestBuilder).andExpect(status().isOk());

        System.out.println(response.getContentAsString());

        System.out.println("*********************************Test for create like in debat controller done *******************");
    }
/*
    @Test
    public void getLikeByEntity() throws Exception{
        int entityId=3;
        EntityType entityType=EntityType.AXE;
        LikeTable likeTable =null;
        LikeWS likeWS=new LikeWS();
        likeWS=likeService.getByEntity(3,entityType);

        when(likeService.getByEntity(3,entityType)).thenReturn(likeWS);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/likes/{entityType}/{entityId}",entityType,entityId)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();
        System.out.println(response.getContentAsString());
        mockMvc.perform(requestBuilder).andExpect(status().isOk());
        System.out.println("*********************************Test for get like by id in AXE controller done *******************");

    }
*/


    private LikeTable getLikeById(String entityType, int entityId) throws Exception {
        List<LikeTable> likeTables = getLikeTableFromJson();
        LikeTable likeTable = likeTables.stream().filter(item->item.getEntityId()==entityId &&  item.getEntityType()==entityType).findFirst().get();

        return likeTable;
    }

    private static String objectToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  List<LikeTable> getLikeTableFromJson() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile("data-json" + File.separator + "liketable.json");

        String text = Files.toString(new File(dataFile.getAbsolutePath()), Charsets.UTF_8);

        Type typeOfObjectsListNew = new TypeToken<List<LikeTable>>() {
        }.getType();
        List<LikeTable> likeTables = GsonUtils.getObjectFromJson(text, typeOfObjectsListNew);

        return likeTables;
    }


    public File getFile(String relativePath) throws Exception {
        File file = new File(getClass().getClassLoader().getResource(relativePath).toURI());
        if(!file.exists()) {
            throw new FileNotFoundException("File:" + relativePath);
        }
        return file;
    }
}
