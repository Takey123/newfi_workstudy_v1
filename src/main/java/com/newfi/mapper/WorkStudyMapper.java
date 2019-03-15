package com.newfi.mapper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public interface WorkStudyMapper {

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

	public List<Map<String, Object>> findUserByStatus(Map<String, Object> map);

	public int updateUserStatus(Map<String, Object> map);

	public void addManager(Map<String, Object> map);

	public void deleteManager(int id);

	public int updateManager(Map<String, Object> map);

	public List<Map<String, Object>> findManager(Map<String, Object> map);
	
	
	public int addAd(Map<String, Object> map);

	public void deleteAd(int id);

	public int updateAd(Map<String, Object> map);

	public int addOrUpdateKeyWord(String str);

	public void deleteNotUseKeyWord();

	public List<Map<String, Object>> findKeyWord(Map<String, Object> map);
	
	public List<Map<String, Object>> findAd(int status);

}
