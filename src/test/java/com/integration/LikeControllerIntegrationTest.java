package com.integration;

import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.entities.LikeTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

/**
 * <h2> The integration test for Like controller</h2>
 * <p>
 * In this test class,
 * the methods :
 * <ul>
 * <li>create a like </li>
 * <li>get all like or unlike of one entity</li>
 * </ul>
 * before  getting started , make sure , the like fonctionnel service is deploy and running as well.
 * you can also test it will the online ruuning service
 * As this test interact directly with the local database, make sure that the specific database has been created
 * and all it's tables.
 * If you have data in the table,make sure that before creating a data with it's id, do not use
 * an existing id.
 * </p>
 */


public class LikeControllerIntegrationTest {
    private static Logger logger = LogManager.getLogger(LikeControllerIntegrationTest.class);
    /**
     *The Base URI of Like fonctionnal service,
     * it can be change with the online URI of this service.
     */
    private String BASE_URI = "http://165.227.69.188:8085/like";
//    private String BASE_URI = "http://localhost:8085/like";

    /**
     * The path of the Like controller, according to this controller implementation
     */
    private String LIKE_PATH ="/likes";

    /**
     * This ID is initialize for create , getById, and update method,
     * you have to change it if you have a save data on this ID otherwise
     * a error or conflit will be note by your test.
     */
    private int entityId = 0;
    
    private int likeId = 0;
    
    private String entityType = EntityType.PROJET.toString();
//    private String entityType = "PROJET";

    /**
     * This method create a new like with the given id
     * @see LikeTable#id
     * <b>you have to set the entityType of
     * the LikeTable if this entity already exists in the database
     * @see LikeTable#entityType()
     * else, the projet  will be created but not wiht the given ID.
     * Also, you have to set the entityId of the LikeTable
     * @see LikeTable#entityId()
     * and this will accure an error in the getById method</b>
     * Note that this method will be the first to execute
     * If every done , it will return a 200 httpStatus code
     * @throws Exception
     */
    @Test(priority = 0, groups = {"LikeTableTest"})
    public void createLikeTest() throws Exception{
        
        String createURI = BASE_URI+LIKE_PATH;
        LikeTable likeTable = getLikeById(entityType,1);
        likeTable.setId(1);
        likeTable.setEntityId(1);
        likeTable.setEntityType(entityType);
        likeTable.setDateCreation(12032017);
        likeTable.setDateMiseAJour(18052017);
        likeTable.setLikeType(true);
        Gson gson = new Gson();
        String likeTableData = gson.toJson(likeTable);
        Response response = given()
                .log().all()
                .contentType("application/json")
                .body(likeTableData)
                .expect()
                .when()
                .post(createURI);

//        System.out.println("\n\n vue: "+response.getBody().asString()+"\n\n");

//        likeId = gson.fromJson(response.getBody().asString(),LikeTable.class ).getId();
        likeId = Integer.parseInt(response.getBody().asString());


        logger.debug(response.getBody().prettyPrint());

        Assert.assertEquals(response.statusCode(), 200) ;
        
    }

    /**
     *
     * @throws Exception
     */
    //@Test
    public void getLikeByEntityTest() throws Exception{
        
        String getLikeById = BASE_URI+LIKE_PATH+"/"+entityType;

        Response response = given()
                .log().all()
                .contentType("application/json")
                .expect()
                .when()
                .get(getLikeById);

        logger.debug(response.getBody().prettyPrint());

        Assert.assertEquals(response.statusCode(), 200) ;

    }
    
    
    private LikeTable getLikeById(String entityType, int entityId) throws Exception {
        List<LikeTable> likeTables = getLikeTableFromJson();
//        System.out.println(entityType+"     et    "+entityId);
        LikeTable likeTable = likeTables.stream().filter(item->item.getEntityId()==entityId &&  item.getEntityType().equals(entityType)).findFirst().get();
        
        return likeTable;
    }

    /**
     * Convert a liketable Object data list to a liketable Json
     * @return a LikeTable object
     */
    private static String objectToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a liketable json data to a liketable objet list
     * this json file is in resources
     * @return a list of LikeTable in this json file
     * @throws Exception
     */
    public  List<LikeTable> getLikeTableFromJson() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile("data-json" + File.separator + "liketable.json");

        String text = Files.toString(new File(dataFile.getAbsolutePath()), Charsets.UTF_8);

        Type typeOfObjectsListNew = new TypeToken<List<LikeTable>>() {
        }.getType();
        List<LikeTable> likeTables = GsonUtils.getObjectFromJson(text, typeOfObjectsListNew);

        return likeTables;
    }

    /**
     * Convert a relative path file into a File Object type
     * @param relativePath
     * @return  File
     * @throws Exception
     */
    public File getFile(String relativePath) throws Exception {
        File file = new File(getClass().getClassLoader().getResource(relativePath).toURI());
        if(!file.exists()) {
            throw new FileNotFoundException("File:" + relativePath);
        }
        return file;
    }

}
