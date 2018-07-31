package com.bootcamp.services;

import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.models.Rule;
import com.bootcamp.commons.ws.usecases.pivotone.LikeWS;
import com.bootcamp.crud.LikeTableCRUD;
import com.bootcamp.entities.Censure;
import com.bootcamp.entities.LikeTable;
import com.rintio.elastic.client.ElasticClient;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Created by darextossa on 11/27/17.
 */
@Component
public class LikeService implements DatabaseConstants {
    ElasticClient elasticClient ;
    private List<LikeTable> likes;

    @PostConstruct
    public void init(){
        this.likes = new ArrayList<>();
        elasticClient = new ElasticClient();
    }

    public List<LikeTable> lire() throws Exception{
        if(this.likes.isEmpty())
            getAllLike();
        return  this.likes;
    }
    /**
     * Insert the given like entity in the database
     *
     * @param likeTable
     * @return like id
     * @throws SQLException
     */
    public LikeTable create(LikeTable likeTable) throws Exception {
        likeTable.setDateCreation(System.currentTimeMillis());
        LikeTableCRUD.create(likeTable);
        createAllIndexLike();
        return likeTable;
    }

    /**
     * Update the given like entity in the database
     *
     * @param likeTable
     * @return like id
     * @throws SQLException
     */
    public LikeTable update(LikeTable likeTable) throws Exception {
        LikeTableCRUD.update(likeTable);
        createAllIndexLike();
        return likeTable;
    }

    /**
     * Delete a like entity in the database
     *
     * @param id
     * @return likeEntity
     * @throws SQLException
     */
    public boolean delete(int id) throws Exception {
        LikeTable likeTable = read(id);
        if( LikeTableCRUD.delete(likeTable))
        createAllIndexLike();
        return true;
    }

    /**
     * Get a like entity by its id
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public LikeTable read(int id) throws Exception {
//        Criterias criterias = new Criterias();
//        criterias.addCriteria(new Criteria("id", "=", id));
//        List<LikeTable> likeTables = LikeTableCRUD.read(criterias);
        return lire().stream().filter(t->t.getId()==id).findFirst().get();
    }

    /**
     * Get all the likes and unlikes of a given entity
     *
     * @param entityId
     * @param entityType
     * @return likeWS (likes and unlikes count)
     * @throws SQLException
     */
    public LikeWS getByEntity(int entityId, EntityType entityType) throws Exception {
        LikeWS likeWS = new LikeWS();
        int like = 0;
        int unlike = 0;
        like = countEntity(entityId, entityType, true);
        unlike = countEntity(entityId, entityType, false);
        likeWS.setLike(like);
        likeWS.setUnlike(unlike);
        return likeWS;
    }

    /**
     * Count the like or unlike of a given entity
     *
     * @param entityId
     * @param entityType
     * @param b
     * @return count
     * @throws SQLException
     */
    public int countEntity(int entityId, EntityType entityType, boolean b) throws Exception {
//        Criterias criterias = new Criterias();
//        criterias.addCriteria(new Criteria(new Rule("entityId", "=", entityId), " AND "));
//        criterias.addCriteria(new Criteria(new Rule("likeType", "=", b), " AND "));
//        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
//        return LikeTableCRUD.read(criterias).size();
//        int nb =   getAllLike().stream().filter(t->t.getEntityType().equalsIgnoreCase(entityType.toString()) && t.getEntityId()==entityId && t.equals(b)).collect(Collectors.toList()).size();
//        System.out.println("LIKE : "+nb);
//        return nb;
        return  lire().stream().filter(t->t.getEntityType().equalsIgnoreCase(entityType.toString()))
                .collect(Collectors.toList())
                .stream().filter(o->o.getEntityId()==entityId).collect(Collectors.toList())
                .stream().filter(r->r.isLikeType()==b).collect(Collectors.toList()).size();
    }

    public int countLikeOrUnlikeForEntity(EntityType entityType, boolean b) throws Exception {
//        Criterias criterias = new Criterias();
//        criterias.addCriteria(new Criteria(new Rule("likeType", "=", b), " AND "));
//        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
//        return LikeTableCRUD.read(criterias).size();
        return (int)lire().stream().filter(t->t.getEntityType().equalsIgnoreCase(entityType.toString())).collect(Collectors.toList())
                .stream().filter(t->t.isLikeType()==b).count();
    }

    public int countLike(EntityType entityType, boolean b) throws Exception {

        int nbLike = 0;
        nbLike = countLikeOrUnlikeForEntity(entityType, true);
        return nbLike;
    }

    public int countUnlike(EntityType entityType, boolean b) throws Exception {

        int nbUnlike = 0;
        nbUnlike = countLikeOrUnlikeForEntity(entityType, false);
        return nbUnlike;
    }

    public List<LikeTable> getAllLikeByEntity(EntityType entityType) throws Exception {
//        Criterias criterias = new Criterias();
//        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
//        return LikeTableCRUD.read(criterias);
        return lire().stream().filter(t->t.getEntityType().equalsIgnoreCase(entityType.toString())).collect(Collectors.toList());
    }

    public List<LikeTable> getAllLikeByEntity(EntityType entityType, String startDate, String endDate) throws SQLException, ParseException {
        EntityManager em = Persistence.createEntityManagerFactory(DatabaseConstants.PERSISTENCE_UNIT).createEntityManager();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        long dateDebut = formatter.parse(startDate).getTime();
        long dateFin = formatter.parse(endDate).getTime();
        TypedQuery<LikeTable> query = em.createQuery(
                "SELECT e FROM LikeTable e WHERE e.entityType =?1 AND e.dateCreation BETWEEN ?2 AND ?3", LikeTable.class);
        List<LikeTable> likes = query.setParameter(1, entityType.name())
                                     .setParameter(2, dateDebut)
                                     .setParameter(3, dateFin)
                                     .getResultList();
        return likes;
    }

    public List<LikeTable> getAll() throws Exception, IllegalAccessException, DatabaseException, InvocationTargetException {
        return lire();
    }

    public List<LikeTable> getAllLike() throws Exception{
//        ElasticClient elasticClient = new ElasticClient();
        List<Object> objects = elasticClient.getAllObject("likes");
        ModelMapper modelMapper = new ModelMapper();
        List<LikeTable> rest = new ArrayList<>();
        for(Object obj:objects){
            rest.add(modelMapper.map(obj,LikeTable.class));
        }
        this.likes = rest;
        return rest;
    }
    public boolean createAllIndexLike()throws Exception{
        List<LikeTable> likes = LikeTableCRUD.read();
        for (LikeTable like : likes){
            elasticClient.creerIndexObjectNative("likes","like",like,like.getId());
        }
        return true;
    }

}
