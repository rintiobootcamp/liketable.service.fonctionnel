package com.bootcamp.controllers;

import com.bootcamp.commons.enums.EntityType;
import com.bootcamp.commons.ws.usecases.pivotone.LikeWS;
import com.bootcamp.entities.LikeTable;
import com.bootcamp.services.LikeService;
import com.bootcamp.version.ApiVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bello
 */
@RestController("LikeController")
@RequestMapping("/likes")
@Api(value = "Like API", description = "Like API")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    LikeService likeTableService;

    @Autowired
    HttpServletRequest request;

    /**
     * Insert a like (or unlike) in the database
     *
     * @param likeTable
     * @return like id
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Create a new likeTable", notes = "Create a new likeTable")
    public ResponseEntity<LikeTable> create(@RequestBody @Valid LikeTable likeTable) throws SQLException {
        LikeTable likeTableRest = likeTableService.create(likeTable);
        return new ResponseEntity<>(likeTableRest, HttpStatus.OK);
    }

    /**
     * Get a like by its id
     *
     * @param id
     * @return like entity
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read a like", notes = "Read a like")
    public ResponseEntity<LikeTable> read(@PathVariable(name = "id") int id) {

        LikeTable likeTable = new LikeTable();
        HttpStatus httpStatus = null;

        try {
            likeTable = likeTableService.read(id);
            httpStatus = HttpStatus.OK;
        } catch (SQLException ex) {
            Logger.getLogger(LikeController.class.getName()).log(Level.SEVERE, null, ex);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(likeTable, httpStatus);
    }

    /**
     * Get all the likes and unlikes of the given entity
     *
     * @param entityId
     * @param entityType
     * @return likes list
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{entityType}/{entityId}")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read a like", notes = "Read a like")
    public ResponseEntity<LikeWS> readByEntity(@PathVariable("entityId") int entityId, @PathVariable("entityType") String entityType) {
        EntityType entite = EntityType.valueOf(entityType);
        LikeWS likeTable = new LikeWS();
        HttpStatus httpStatus = null;

        try {
            likeTable = likeTableService.getByEntity(entityId, entite);
            httpStatus = HttpStatus.OK;
        } catch (SQLException ex) {
            Logger.getLogger(LikeController.class.getName()).log(Level.SEVERE, null, ex);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(likeTable, httpStatus);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/stats/{entityType}")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read all debat on entity", notes = "Read all debat on entity")
    public ResponseEntity<List<LikeTable>> readAllLikeByEntity(@PathVariable("entityType") String entityType, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        EntityType entite = EntityType.valueOf(entityType);
        List<LikeTable> likes = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (startDate.equals("") && endDate.equals("")) {
                likes = likeTableService.getAllLikeByEntity(entite);
                httpStatus = HttpStatus.OK;
            } else if (!startDate.equals("") && !endDate.equals("")) {
                likes = likeTableService.getAllLikeByEntity(entite, startDate, endDate);
                httpStatus = HttpStatus.OK;
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(LikeController.class.getName()).log(Level.SEVERE, null, ex);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(likes, httpStatus);

    }

}
