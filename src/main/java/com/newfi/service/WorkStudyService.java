package com.newfi.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WorkStudyService {

	int addWorkStudy(Map<String, Object> map);

	void deleteWorkStudy(Map<String, Object> map);

	int updateWorkStudy(Map<String, Object> map);

	List<Map<String, Object>> findWorkStudy(Map<String, Object> map);
	
	/**
	 * 用户添加收藏
	 * @param map
	 * @return
	 */
	public int addCollectionForItem(Map<String, Object> map);

	/**
	 * 用户删除收藏
	 * @param map
	 * @return
	 */
	public int deleteCollectionForItem(Map<String, Object> map);

	/**
	 * 通过用户ID，查找收藏夹
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> findCollectionByUserId(int userId);

	void deleteCollectionByItemId(int id);

	List<Map<String, Object>> findAd(Map<String, Object> map);

	public Map<String, Object> findUserByUserId(Map<String, Object> map2);

	public void insertUser(Map<String, Object> map2);
	
	
	public void findUserByStatus(HttpServletRequest request, HttpServletResponse response);
	
	public void updateUserStatus(HttpServletRequest request, HttpServletResponse response);

	public void addManager(HttpServletRequest request, HttpServletResponse response);

	public void deleteManager(HttpServletRequest request, HttpServletResponse response);

	public void updateManager(HttpServletRequest request, HttpServletResponse response);

	public void findManager(HttpServletRequest request, HttpServletResponse response);
	
	
	public void addAd(HttpServletRequest request, HttpServletResponse response);

	public void deleteAd(HttpServletRequest request, HttpServletResponse response);

	public void updateAd(HttpServletRequest request, HttpServletResponse response);

	public void addOrUpdateKeyWord(HttpServletRequest request, HttpServletResponse response);

	public void findKeyWord(HttpServletRequest request, HttpServletResponse response);
	
	public void findAd(HttpServletRequest request, HttpServletResponse response);

	public List<Map<String, Object>> findKeyWord(Map<String, Object> map4);
}
