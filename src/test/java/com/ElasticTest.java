package com;

import com.bootcamp.crud.CensureCRUD;
import com.bootcamp.crud.LikeTableCRUD;
import com.bootcamp.entities.Censure;
import com.bootcamp.entities.LikeTable;
import com.rintio.elastic.client.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

public class ElasticTest {
    private final Logger LOG = LoggerFactory.getLogger(ElasticTest.class);


    @Test
    public void createIndexLike()throws Exception{
        ElasticClient elasticClient = new ElasticClient();
        List<LikeTable> likeTables = LikeTableCRUD.read();
        for (LikeTable likeTable : likeTables){
            elasticClient.creerIndexObjectNative("likes","like",likeTable,likeTable.getId());
            LOG.info("like "+likeTable.getId()+" created");
        }
    }
}
