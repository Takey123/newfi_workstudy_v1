package com.newfi.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newfi.mapper.WorkStudyMapper;
import com.newfi.service.WorkStudyService;
import com.newfi.util.Log4jUtil;
import com.newfi.util.PrintUtil;

import main.util.Encoder;
import main.util.SignUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Service
public class WorkStudyServiceImpl implements WorkStudyService{

	@Autowired
	private WorkStudyMapper workStudyMapper;

	@Override
	public int addWorkStudy(Map<String, Object> map) {
		return workStudyMapper.addWorkStudy(map);
	}

	@Override
	public void deleteWorkStudy(Map<String, Object> map) {
		workStudyMapper.deleteWorkStudy(map);
	}

	@Override
	public int updateWorkStudy(Map<String, Object> map) {
		return workStudyMapper.updateWorkStudy(map);
	}

	@Override
	public List<Map<String, Object>> findWorkStudy(Map<String, Object> map) {
		return workStudyMapper.findWorkStudy(map);
	}

	@Override
	public int addCollectionForItem(Map<String, Object> map) {
		return workStudyMapper.addCollectionForItem(map);
	}

	@Override
	public int deleteCollectionForItem(Map<String, Object> map) {
		return workStudyMapper.deleteCollectionForItem(map);
	}

	@Override
	public List<Map<String, Object>> findCollectionByUserId(int userId) {
		return workStudyMapper.findCollectionByUserId(userId);
	}

	@Override
	public void deleteCollectionByItemId(int id) {
		workStudyMapper.deleteCollectionByItemId(id);

	}

	@Override
	public List<Map<String, Object>> findAd(Map<String, Object> map) {
		return workStudyMapper.findAd(map);
	}

	@Override
	public Map<String, Object> findUserByUserId(Map<String, Object> map2) {
		return workStudyMapper.findUserByUserId(map2);
	}

	@Override
	public void insertUser(Map<String, Object> map2) {
		workStudyMapper.insertUser(map2);
	}

	
	@Override
	public void findUserByStatus(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					int currentPage = contentStrJson.optInt("currentPage");
					int pageSize = contentStrJson.optInt("pageSize");

					currentPage = (currentPage-1) * pageSize;

					Map<String,Object> map = new HashMap<>();
					map.put("keyWord", contentStrJson.optString("keyWord"));
					map.put("status", contentStrJson.optString("status"));
					List<Map<String,Object>> list2 = workStudyMapper.findUserByStatus(map);

					map.put("currentPage", currentPage);
					map.put("pageSize", pageSize);
					List<Map<String,Object>> list = workStudyMapper.findUserByStatus(map);
					sendJson.put("code", 1);
					sendJson.put("list", list);
					sendJson.put("SUM", list2.size());
					sendJson.put("msg", "你查询用户成功啦！！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "findUserByStatus");
	}
	
	
	@Override
	public void updateUserStatus(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					Map<String,Object> map = new HashMap<>();
					map.put("userId", contentStrJson.optString("userId"));
					map.put("status", contentStrJson.optString("status"));
					int result = workStudyMapper.updateUserStatus(map);
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你修改用户状态成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你修改用户状态失败啦！！");
					}

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "updateUserStatus");
	}
	
	
	@Override
	public void addManager(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					Map<String, Object> map = new HashMap<>();
					map.put("userId", contentStrJson.optString("userId"));
					map.put("userName", contentStrJson.optString("userName"));
					map.put("tel", contentStrJson.optString("tel"));
					map.put("status", contentStrJson.optString("status"));
					map.put("areaId", contentStrJson.optString("areaId"));
					map.put("superManager", contentStrJson.optString("superManager"));
					workStudyMapper.addManager(map);
					
					sendJson.put("code", 1);
					sendJson.put("msg", "你添加管理员成功啦！！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "addManager");
	}

	@Override
	public void deleteManager(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					String idList = contentStrJson.optString("idList");
					System.out.println(idList);
					JSONArray array = JSONArray.fromObject(idList);
					for (Object object : array) {
						System.out.println(object);
						String idStr = object.toString();
						int id = Integer.parseInt(idStr);
						
						workStudyMapper.deleteManager(id);

					}
					sendJson.put("code", 1);
					sendJson.put("msg", "你批量删除管理员成功啦！！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "deleteManager");
	}

	@Override
	public void updateManager(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					String idList = contentStrJson.optString("idList");
					
					Map<String, Object> map = new HashMap<>();
					map.put("id", contentStrJson.optString("id"));
					map.put("userId", contentStrJson.optString("userId"));
					map.put("userName", contentStrJson.optString("userName"));
					map.put("tel", contentStrJson.optString("tel"));
					map.put("status", contentStrJson.optString("status"));
					map.put("areaId", contentStrJson.optString("areaId"));
					map.put("superManager", contentStrJson.optString("superManager"));
					
					int result = workStudyMapper.updateManager(map);
					
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你更新管理员成功啦！！");

					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你更新管理员失败啦！！");

					}
					
				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "updateManager");
	}

	@Override
	public void findManager(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					int currentPage = contentStrJson.optInt("currentPage");
					int pageSize = contentStrJson.optInt("pageSize");

					currentPage = (currentPage-1) * pageSize;
					
					Map<String, Object> map = new HashMap<>();
					map.put("areaId", contentStrJson.optString("areaId"));
					map.put("status", contentStrJson.optString("status"));
					map.put("keyWord", contentStrJson.optString("keyWord"));
					List<Map<String, Object>> list2 = workStudyMapper.findManager(map);
					map.put("currentPage", currentPage);
					map.put("pageSize", pageSize);

					List<Map<String, Object>> list = workStudyMapper.findManager(map);
					

					sendJson.put("code", 1);
					sendJson.put("list", list);
					sendJson.put("SUM", list2.size());
					sendJson.put("msg", "你查找管理员成功啦！！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "BatchDeleteCommentForItem");
	}
	
	
	
	@Override
	public void addAd(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					
					Map<String, Object> map = new HashMap<>();
					map.put("adTitle", contentStrJson.optString("adTitle"));
					map.put("adPhoto", contentStrJson.optString("adPhoto"));
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
					map.put("adCreateTime", sdf.format(date));
					map.put("adClickLink", contentStrJson.optString("adClickLink"));
					map.put("status", 1);
					
					int result = workStudyMapper.addAd(map);
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你添加广告成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你添加广告失败啦！！");
					}

					

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "addAd");
		
	}

	@Override
	public void deleteAd(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					String idList = contentStrJson.optString("idList");
					System.out.println(idList);
					JSONArray array = JSONArray.fromObject(idList);
					for (Object object : array) {
						System.out.println(object);
						String idStr = object.toString();
						int id = Integer.parseInt(idStr);
						workStudyMapper.deleteAd(id);
					}

					sendJson.put("code", 1);
					sendJson.put("msg", "你删除广告成功啦！！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "deleteAd");
		
	}

	@Override
	public void updateAd(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					Map<String, Object> map = new HashMap<>();
					map.put("id", contentStrJson.optString("id"));
					map.put("adTitle", contentStrJson.optString("adTitle"));
					map.put("adPhoto", contentStrJson.optString("adPhoto"));
					map.put("adClickLink", contentStrJson.optString("adClickLink"));
					map.put("status", contentStrJson.optString("status"));
					
					int result = workStudyMapper.updateAd(map);
					
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你修改广告成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你修改广告失败啦！！");
					}

					

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "BatchDeleteCommentForItem");
		
	}
	
	
	@Override
	public void findAd(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					int status = contentStrJson.optInt("status");

					List<Map<String, Object>> list = workStudyMapper.findAd(status);

					sendJson.put("code", 1);
					sendJson.put("list", list);
					sendJson.put("msg", "你查询广告成功！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "findAd");

	}

	@Override
	public void addOrUpdateKeyWord(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					workStudyMapper.deleteNotUseKeyWord();
					String keyWord = contentStrJson.optString("keyWord");
					String[] split = keyWord.split("\\、");
					for (String string : split) {
						System.out.println(string);
						workStudyMapper.addOrUpdateKeyWord(string);
					}
						sendJson.put("code", 1);
						sendJson.put("msg", "你添加禁用文字成功！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "addOrUpdateKeyWord");
		
	}

	@Override
	public void findKeyWord(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		Log4jUtil.info("入口参数："+params);
		//判断参数是否为空
		try{
			if(params != null && !"".equals(params)){
				//如果不为空
				//将params转成Json对象
				JSONObject paramsJson = JSONObject.fromObject(params);
				//校验签名
				JSONObject checkSignJson = SignUtil.checkSign(paramsJson);
				//判断签名返回的结果
				if("1".equals("1")){
					//如果校验成功
					//解析paramsJson，获取里面参数
					//	String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);

					Map<String, Object> map = new HashMap<>();
					map.put("keyWord", contentStrJson.optString("keyWord"));
					List<Map<String,Object>> list = workStudyMapper.findKeyWord(map);

					sendJson.put("code", 1);
					sendJson.put("list", list);
					sendJson.put("msg", "你查询禁用文字成功！");

				}else{
					//如果校验失败
					sendJson.put("code", checkSignJson.optString("code"));
					sendJson.put("msg", checkSignJson.optString("msg"));
				}	
			}else{
				//如果为空
				sendJson.put("code", 2);
				sendJson.put("msg", "你传进来的参数为空");
			}
		}catch(JSONException e){
			sendJson.put("code", 10004);
			sendJson.put("msg", "你传进来的参数格式不对哦！！！");
		}catch(Exception e){
			sendJson.put("code", 10005);
			sendJson.put("msg", "系统错误！！！");
			e.printStackTrace();
		}

		//Log4jUtil.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "findAd");
		
	}

	@Override
	public List<Map<String, Object>> findKeyWord(Map<String, Object> map4) {
		List<Map<String,Object>> list = workStudyMapper.findKeyWord(map4);
		return list;
	}
}
