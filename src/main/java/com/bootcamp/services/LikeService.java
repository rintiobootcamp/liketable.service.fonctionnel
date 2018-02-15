package com.bootcamp.services;

import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.models.Rule;
import com.bootcamp.commons.ws.usecases.pivotone.LikeWS;
import com.bootcamp.crud.LikeTableCRUD;
import com.bootcamp.entities.LikeTable;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Created by darextossa on 11/27/17.
 */
@Component
public class LikeService implements DatabaseConstants {

    /**
     * Insert the given like entity in the database
     *
     * @param likeTable
     * @return like id
     * @throws SQLException
     */
    public LikeTable create(LikeTable likeTable) throws SQLException {
        likeTable.setDateCreation(System.currentTimeMillis());
        LikeTableCRUD.create(likeTable);
        return likeTable;
    }

    /**
     * Update the given like entity in the database
     *
     * @param likeTable
     * @return like id
     * @throws SQLException
     */
    public LikeTable update(LikeTable likeTable) throws SQLException {
        LikeTableCRUD.update(likeTable);
        return likeTable;
    }

    /**
     * Delete a like entity in the database
     *
     * @param id
     * @return likeEntity
     * @throws SQLException
     */
    public boolean delete(int id) throws SQLException {
        LikeTable likeTable = read(id);
        return LikeTableCRUD.delete(likeTable);
    }

    /**
     * Get a like entity by its id
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public LikeTable read(int id) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        List<LikeTable> likeTables = LikeTableCRUD.read(criterias);
        return likeTables.get(0);
    }

    /**
     * Get all the likes and unlikes of a given entity
     *
     * @param entityId
     * @param entityType
     * @return likeWS (likes and unlikes count)
     * @throws SQLException
     */
    public LikeWS getByEntity(int entityId, EntityType entityType) throws SQLException {
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
    public int countEntity(int entityId, EntityType entityType, boolean b) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria(new Rule("entityId", "=", entityId), " AND "));
        criterias.addCriteria(new Criteria(new Rule("likeType", "=", b), " AND "));
        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
        return LikeTableCRUD.read(criterias).size();
    }

    public int countLikeOrUnlikeForEntity(EntityType entityType, boolean b) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria(new Rule("likeType", "=", b), " AND "));
        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
        return LikeTableCRUD.read(criterias).size();
    }

    public int countLike(EntityType entityType, boolean b) throws SQLException {

        int nbLike = 0;
        nbLike = countLikeOrUnlikeForEntity(entityType, true);
        return nbLike;
    }

    public int countUnlike(EntityType entityType, boolean b) throws SQLException {

        int nbUnlike = 0;
        nbUnlike = countLikeOrUnlikeForEntity(entityType, false);
        return nbUnlike;
    }

    public List<LikeTable> getAllLikeByEntity(EntityType entityType) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
        return LikeTableCRUD.read(criterias);
    }

    public List<LikeTable> getAllLikeByEntity(EntityType entityType, String startDate, String endDate) throws SQLException, ParseException {
        EntityManager em = Persistence.createEntityManagerFactory(DatabaseConstants.PERSISTENCE_UNIT).createEntityManager();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        long dateDebut = formatter.parse(startDate).getTime();
        long dateFin = formatter.parse(endDate).getTime();
        TypedQuery<LikeTable> query = em.createQuery(
                "SELECT e FROM LikeTable e WHERE e.entityType =:?1 AND e.dateCreation BETWEEN ?2 AND ?3", LikeTable.class);
        List<LikeTable> likes = query.setParameter(1, entityType)
                                     .setParameter(2, dateFin)
                                     .setParameter(3, dateDebut)
                                     .getResultList();
        return likes;
    }

    public List<LikeTable> getAll() throws SQLException, IllegalAccessException, DatabaseException, InvocationTargetException {
        return LikeTableCRUD.read();
    }

}
