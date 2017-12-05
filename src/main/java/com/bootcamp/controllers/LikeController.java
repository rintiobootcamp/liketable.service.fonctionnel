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
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController("LikeController")
@RequestMapping("/likes")
@Api(value = "Like API", description = "Like API")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    LikeService likeTableService;

    @Autowired
    HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST, value = "/")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Create a new likeTable", notes = "Create a new likeTable")
    public ResponseEntity<Integer> create(@RequestBody @Valid LikeTable likeTable) {

        HttpStatus httpStatus = null;

        int id = -1;
        try {
            id = likeTableService.create(likeTable);
            httpStatus = HttpStatus.OK;
        } catch (SQLException ex) {
            Logger.getLogger(LikeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ResponseEntity<Integer>(id, httpStatus);
    }

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
}
