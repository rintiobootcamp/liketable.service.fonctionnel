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
import java.util.List;

/**
 * Created by darextossa on 11/27/17.
 */
@Component
public class LikeService implements DatabaseConstants {

    public int create(LikeTable likeTable ) throws SQLException {
       likeTable.setDateCreation(System.currentTimeMillis());
               LikeTableCRUD.create(likeTable);
        return likeTable.getId();
    }

    public int update(LikeTable likeTable ) throws SQLException {
        LikeTableCRUD.update(likeTable);
        return likeTable.getId();
    }

    public LikeTable delete(int id) throws SQLException {
        LikeTable likeTable = read(id);
        LikeTableCRUD.delete(likeTable);
        return likeTable;
    }

    public LikeTable read(int id) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        List<LikeTable> likeTables = LikeTableCRUD.read(criterias);
        return likeTables.get(0);
    }

    public LikeWS read(LikeTable likeTable) throws SQLException {
        LikeWS likeWS=new LikeWS();
        if(likeTable.isLikeType())
            likeWS.setLike(1);
        else
            likeWS.setUnlike(1);
        return likeWS;
    }

    public LikeWS  getByEntity(int entityId, EntityType entityType) throws SQLException {
        LikeWS likeWS =new  LikeWS();
        int like=0;
        int unlike=0;
        like = countEntity(entityId,  entityType ,true);
        unlike = countEntity(entityId,  entityType ,false);
        likeWS.setLike(like);
        likeWS.setUnlike(unlike);
        return likeWS;
    }
    
     public int countEntity(int entityId, EntityType entityType ,boolean b) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria(new Rule("entityId", "=", entityId), " AND "));
        criterias.addCriteria(new Criteria(new Rule("likeType", "=", b), " AND "));
        criterias.addCriteria(new Criteria(new Rule("entityType", "=", entityType), null));
        return LikeTableCRUD.read(criterias).size();
    }
    public List<LikeTable> getAll() throws SQLException, IllegalAccessException, DatabaseException, InvocationTargetException {
        return LikeTableCRUD.read();
    }
    
}
