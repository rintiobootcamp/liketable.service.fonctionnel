package com.bootcamp.services;

import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.models.Rule;
import com.bootcamp.commons.ws.usecases.pivotone.LikeWS;
import com.bootcamp.crud.LikeTableCRUD;
import com.bootcamp.entities.LikeTable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

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
    public int create(LikeTable likeTable) throws SQLException {
        likeTable.setDateCreation(System.currentTimeMillis());
        LikeTableCRUD.create(likeTable);
        return likeTable.getId();
    }

    /**
     * Update the given like entity in the database
     *
     * @param likeTable
     * @return like id
     * @throws SQLException
     */
    public int update(LikeTable likeTable) throws SQLException {
        LikeTableCRUD.update(likeTable);
        return likeTable.getId();
    }

    /**
     * Delete a like entity in the database
     *
     * @param id
     * @return likeEntity
     * @throws SQLException
     */
    public LikeTable delete(int id) throws SQLException {
        LikeTable likeTable = read(id);
        LikeTableCRUD.delete(likeTable);
        return likeTable;
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

}
