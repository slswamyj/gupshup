package com.stackroute.gupshup.recommendationservice.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.gupshup.recommendationservice.entity.CircleRecommendation;
import com.stackroute.gupshup.recommendationservice.entity.UserRecommendation;
import com.stackroute.gupshup.recommendationservice.exception.RecommendationException;
import com.stackroute.gupshup.recommendationservice.linkassembler.RecommendationLinkAssembler;
import com.stackroute.gupshup.recommendationservice.service.CircleRecommendationService;
import com.stackroute.gupshup.recommendationservice.service.UserRecommendationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin()
@RestController
@Api(value="RecommendationService", description="Operations pertaining to Recommendation")
@RequestMapping("recommendation")
public class RecommendationController {
	
	@Autowired
	UserRecommendationService userRecommendationService;
	
	@Autowired
	CircleRecommendationService circleRecommendationService;
		
	RecommendationLinkAssembler recommendationLinkAssembler;
	
	@Autowired
	public void setRecommendationLinkAssembler(RecommendationLinkAssembler recommendationLinkAssembler) {
		this.recommendationLinkAssembler = recommendationLinkAssembler;
	}
	
	/*------method to notify user of incorrect URL in GE Tmethod-----*/
	@ApiOperation(value="Get method error path")
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity getRouteError(){
		
		return new ResponseEntity<>("Incorrect URL",HttpStatus.FORBIDDEN);
	}
	
	/*------method to notify user of incorrect URL in POST method-----*/
	@ApiOperation(value="Post method error path")
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	public ResponseEntity postRouteError(){
		return new ResponseEntity<>("Incorrect URL",HttpStatus.FORBIDDEN);
	}
	
	/*------method to notify user of incorrect URL in PUT method-----*/
	@ApiOperation(value="Put method error path")
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity putRouteError(){
		return new ResponseEntity<>("Incorrect URL",HttpStatus.FORBIDDEN);
	}
	
	/*------method to notify user of incorrect URL in DELETE method-----*/
	@ApiOperation(value="Delete method error path")
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity deleteRouteError(){
		return new ResponseEntity<>("Incorrect URL",HttpStatus.FORBIDDEN);
	}
	
	/*--------recommendation method to follow friend of friend with distinct results-----*/
	@ApiOperation(value="Follow User Recommendation")
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public ResponseEntity followFriendOfFriend(@PathVariable String id)
	{
		List<Map<String,String>> list;
		try {
			list = userRecommendationService.followRecommendation(id);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Username incorrect",HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<List<Map<String,String>>>(list,HttpStatus.OK);
	}
	
	/*---------circle subscribe recommendation for a user---------*/
	@ApiOperation(value="Subscribe to Circle Recommendation")
	@RequestMapping(value="/circle/{id}", method=RequestMethod.GET)
	public ResponseEntity subscribeRecommendation(@PathVariable String id){
		List<Map<String,String>> list;;
		try {
			list=circleRecommendationService.subscribeRecommendation(id);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Username incorrect",HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<List<Map<String,String>>>(list,HttpStatus.OK);
	}
	
	/*------method to create a user node in neo4j-------*/
	@ApiOperation(value="Create a user node")
	@RequestMapping(value="/createuser", method=RequestMethod.POST)
	public ResponseEntity createUser(@Valid @RequestBody UserRecommendation userRecommendation, BindingResult bindingResult){
		if(bindingResult.hasErrors())
		{
			return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
		}
		try {
				userRecommendationService.createUser(userRecommendation);
				UserRecommendation u = recommendationLinkAssembler.userRecommendationLinks(userRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>("Username already exists",HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(userRecommendation,HttpStatus.OK);
	}
	
	/*------method to update a user node in neo4j-------*/
	@ApiOperation(value="Update a user")
	@RequestMapping(value="/updateuser", method=RequestMethod.PUT)
	public ResponseEntity updateUser(@Valid @RequestBody UserRecommendation userRecommendation, BindingResult bindingResult){
		UserRecommendation u=null;
		if(bindingResult.hasErrors())
		{
			return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
		}
		try {
			userRecommendationService.updateUser(userRecommendation);
			u = recommendationLinkAssembler.userRecommendationLinks(userRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>("Username incorrect",HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(u,HttpStatus.OK);
	}
	
	/*------method to delete a user node in neo4j-------*/
	@ApiOperation(value="Delete a user")
	@RequestMapping(value="/deleteuser/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@PathVariable String id)
	{
		try {
			userRecommendationService.deleteUser(id);
			//UserRecommendation userRecommendation = userRecommendationService.findUser(id);
			//UserRecommendation u = recommendationLinkAssembler.userRecommendationLinks(userRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Username incorrect",HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>("User Deleted", HttpStatus.OK);
	}
	
	/*------method to create a circle node in neo4j-------*/
	@ApiOperation(value="Create a circle node")
	@RequestMapping(value="/createcircle", method=RequestMethod.POST)
	public ResponseEntity createCircle(@Valid @RequestBody CircleRecommendation circleRecommendation, BindingResult bindingResult){
		if(bindingResult.hasErrors())
		{
			return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
		}
		
		try {
			circleRecommendationService.createCircle(circleRecommendation);
			CircleRecommendation c = recommendationLinkAssembler.circleRecommendationLinks(circleRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Circle ID already exists", HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(circleRecommendation, HttpStatus.OK);
	}
	
	/*------method to delete a circle node in neo4j-------*/
	@ApiOperation(value="Delete a circle")
	@RequestMapping(value="/deletecircle/{id}", method=RequestMethod.DELETE)
	public ResponseEntity deleteCircle(@PathVariable String id)
	{
		try {
			circleRecommendationService.deleteCircle(id);
			//CircleRecommendation circleRecommendation = circleRecommendationService.findCircle(id);
			//CircleRecommendation c = recommendationLinkAssembler.circleRecommendationLinks(circleRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Circle ID incorrect",HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>("Circle deleted",HttpStatus.OK);
	}
	
	/*------method to update a circle node in neo4j-------*/
	@ApiOperation(value="Update a circle")
	@RequestMapping(value="/updatecircle", method=RequestMethod.PUT)
	public ResponseEntity updateCircle(@Valid @RequestBody CircleRecommendation circleRecommendation, BindingResult bindingResult){
		if(bindingResult.hasErrors())
		{
			return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
		}
		
		try {
			circleRecommendationService.updateCircle(circleRecommendation);
			CircleRecommendation c = recommendationLinkAssembler.circleRecommendationLinks(circleRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Circle ID incorrect",HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(circleRecommendation,HttpStatus.OK);
	}
	
	/*----------method to create a follow relationship in neo4j when user follows another user-------*/
	@ApiOperation(value="Follow user")
	@RequestMapping(value="/follows/{id1}/{id2}", method=RequestMethod.GET)
	public ResponseEntity follows(@PathVariable String id1, @PathVariable String id2){
		try {
			userRecommendationService.follows(id1,id2);
			//UserRecommendation userRecommendation = userRecommendationService.findUser(id1);
			//UserRecommendation u = recommendationLinkAssembler.userRecommendationLinks(userRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Username incorrect", HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(id1+" follows "+id2,HttpStatus.OK);
	}
	
	/*----------method to create a subscribed relationship in neo4j when user subscribes to a circle-------*/
	@ApiOperation(value="Subscribe to a Circle")
	@RequestMapping(value="/subscribed/{id1}/{id2}", method=RequestMethod.GET)
	public ResponseEntity subscribed(@PathVariable String id1, @PathVariable String id2){
		try {
			circleRecommendationService.subscribed(id1,id2);
			//CircleRecommendation circleRecommendation = circleRecommendationService.findCircle(id2);
			//CircleRecommendation c = recommendationLinkAssembler.circleRecommendationLinks(circleRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Circle ID or user ID incorrect",HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(id1+" subscribed to "+id2,HttpStatus.OK);
	}
	
	/*--------method to delete subscribe relationship in neo4j when user wants to leave a circle-------*/
	@ApiOperation(value="Unsubscribe from a Circle")
	@RequestMapping(value="/leavecircle/{id1}/{id2}", method=RequestMethod.GET)
	public ResponseEntity leaveCircle(@PathVariable String id1, @PathVariable String id2){
		try {
			circleRecommendationService.leaveCircle(id1, id2);
			//CircleRecommendation circleRecommendation = circleRecommendationService.findCircle(id2);
			//CircleRecommendation c = recommendationLinkAssembler.circleRecommendationLinks(circleRecommendation);
		} catch (RecommendationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("Circle ID or user ID incorrect",HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(id1+" unsubcribed from "+id2,HttpStatus.OK);
	}

}

