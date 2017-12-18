package com.service;

import com.bootcamp.application.Application;
import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.crud.LikeTableCRUD;
import com.bootcamp.entities.LikeTable;
import com.bootcamp.services.LikeService;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

//import org.junit.Test;
//import org.testng.annotations.Test;

/**
 * Created by darextossa on 12/9/17.
 */

@RunWith(PowerMockRunner.class)
@WebMvcTest(value = LikeTable.class, secure = false)
@ContextConfiguration(classes = {Application.class})
@PrepareForTest(LikeTableCRUD.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;

    @Test
    public void getAllLike() throws Exception {
        System.out.println("eee");
        List<LikeTable> likeTables = loadDatalikeTableFromJsonFile();
        PowerMockito.mockStatic(LikeTableCRUD.class);
        Mockito.
                when(LikeTableCRUD.read()).thenReturn(likeTables);
        List<LikeTable> resultlikeTables = likeService.getAll();
        Assert.assertEquals(likeTables.size(), resultlikeTables.size());


    }

    @Test
    public void create() throws Exception {
        List<LikeTable> likeTables = loadDatalikeTableFromJsonFile();
        LikeTable likeTable = likeTables.get(1);

        PowerMockito.mockStatic(LikeTableCRUD.class);
        Mockito.
                when(LikeTableCRUD.create(likeTable)).thenReturn(true);
    }

    @Test
    public void delete() throws Exception {
        List<LikeTable> likeTables = loadDatalikeTableFromJsonFile();
        LikeTable likeTable = likeTables.get(1);

        PowerMockito.mockStatic(LikeTableCRUD.class);
        Mockito.
                when(LikeTableCRUD.delete(likeTable)).thenReturn(true);

    }

    @Test
    public void update() throws Exception {
        List<LikeTable> likeTables = loadDatalikeTableFromJsonFile();
        LikeTable likeTable = likeTables.get(1);

        PowerMockito.mockStatic(LikeTableCRUD.class);
        Mockito.
                when(LikeTableCRUD.update(likeTable)).thenReturn(true);
    }

    public File getFile(String relativePath) throws Exception {

        File file = new File(getClass().getClassLoader().getResource(relativePath).toURI());

        if (!file.exists()) {
            throw new FileNotFoundException("File:" + relativePath);
        }

        return file;
    }

    public List<LikeTable> loadDatalikeTableFromJsonFile() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile("data-json" + File.separator + "liketable.json");

        String text = Files.toString(new File(dataFile.getAbsolutePath()), Charsets.UTF_8);

        Type typeOfObjectsListNew = new TypeToken<List<LikeTable>>() {
        }.getType();
        List<LikeTable> likeTable = GsonUtils.getObjectFromJson(text, typeOfObjectsListNew);

        return likeTable;
    }

}