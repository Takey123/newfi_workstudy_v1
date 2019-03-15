package com.newfi.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newfi.service.WorkStudyService;
import com.newfi.util.Log4jUtil;
import com.newfi.util.PrintUtil;

import main.util.Encoder;
import main.util.SignUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@RestController
public class WorkStudyController {
	
	Logger logger = LoggerFactory.getLogger(WorkStudyController.class);
	
	@Autowired
	private WorkStudyService workStudyService;

	/**
	 * 添加勤工俭学信息
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userName":"takey","userImage":"http://aagg","tag":"软件工程","areaId":"10000","title":"深大图书馆","salary":"100","period":"1","negotiable":"2","recruitmentType":"1","telephoneNumber":"15338449700","adress":"深圳大学图书馆","positionDetails":"图书馆管理员，整理图书","recruitmentUnit":"深圳大学","nature":"1","scale":"1","userId":"255825"}}
	 * @param request
	 * @param response
	 */
	@RequestMapping("addWorkStudy")
	public void addWorkStudy(HttpServletRequest request,HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		logger.info("入口参数："+params);
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
					String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);
					//==================
					String userId = contentStrJson.optString("userId");
					String username = contentStrJson.optString("userName");
					String tel = contentStrJson.optString("telephoneNumber");
					Map<String,Object> map2 = new HashMap<>();
					map2.put("userId", userId);
					//通过userId去查看，是否存在该用户
					Map<String,Object> map3 = workStudyService.findUserByUserId(map2);
					if(map3 != null) {
						//如果用户存在
						Object object = map3.get("status");
						String status = object.toString();
						if("0".equals(status)) {
							sendJson.put("code", 1);
							sendJson.put("msg", "该用户没有发布权限！！");
							PrintUtil.print(request, response, sendJson, "addItem");
							return;
						}
					}else {
						//如果用户不存在，添加该用户
						map2.put("username", username);
						map2.put("tel", tel);
						map2.put("status", 1);
						workStudyService.insertUser(map2);
					}
					
					//==================
					
					//======================
					String title = contentStrJson.optString("title");
					String content = contentStrJson.optString("positionDetails");
					//查询禁用关键字
					Map<String, Object> map4 = new HashMap<>();
					List<Map<String,Object>> list = workStudyService.findKeyWord(map4);
					if(list.size()>0) {
						for (Map<String, Object> map : list) {
							Object object = map.get("keyWord");
							String keyWord = object.toString();
							if(title.contains(keyWord)) {
								sendJson.put("code", 1);
								sendJson.put("msg", "标题或内容包含禁用词！！");
								PrintUtil.print(request, response, sendJson, "addItem");
								return;
							}
							if(content.contains(keyWord)) {
								sendJson.put("code", 1);
								sendJson.put("msg", "标题或内容包含禁用词！！");
								PrintUtil.print(request, response, sendJson, "addItem");
								return;
							}
						}
					}
					//======================
					
					
					Map<String,Object> map = new HashMap<>();
					map.put("title", contentStrJson.optString("title"));
					map.put("salary", contentStrJson.optString("salary"));
					map.put("period", contentStrJson.optString("period"));
					map.put("negotiable", contentStrJson.optString("negotiable"));
					map.put("recruitmentType", contentStrJson.optString("recruitmentType"));
					map.put("telephoneNumber", contentStrJson.optString("telephoneNumber"));
					map.put("adress", contentStrJson.optString("adress"));
					map.put("positionDetails", contentStrJson.optString("positionDetails"));
					map.put("recruitmentUnit", contentStrJson.optString("recruitmentUnit"));
					map.put("nature", contentStrJson.optString("nature"));
					map.put("scale", contentStrJson.optString("scale"));
					map.put("areaId", contentStrJson.optString("areaId"));
					map.put("tag", contentStrJson.optString("tag"));
					map.put("userName", contentStrJson.optString("userName"));
					map.put("userImage", contentStrJson.optString("userImage"));
					
					SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HH:mm:ss");
					Date date = new Date();
					String createTime = sdf.format(date);
					
					map.put("creatTime", createTime);
					map.put("userId", contentStrJson.optString("userId"));
					map.put("clickNum", 0);

					//---调用service----
					int result = workStudyService.addWorkStudy(map);
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你添加勤工俭学信息成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你添加勤工俭学信息失败啦！！");
					}
					//------------------

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
			e.printStackTrace();
			sendJson.put("code", -1);
			sendJson.put("msg", "系统错误!");
		}

		logger.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "addWorkStudy");
	}
	
	
	/**
	 * 删除勤工俭学信息
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"idList":"[1,2]"}}
	 * @param request
	 * @param response
	 */
	@RequestMapping("deleteWorkStudy")
	public void deleteWorkStudy(HttpServletRequest request,HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		logger.info("入口参数："+params);
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
					String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);
					Map<String,Object> map = new HashMap<>();
					String idList = contentStrJson.optString("idList");
					JSONArray jsonArray = JSONArray.fromObject(idList);
					for (Object object : jsonArray) {
						String id = object.toString();
						map.put("id", id);
						//删除收藏该商品的记录
						workStudyService.deleteCollectionByItemId(Integer.parseInt(id));
						workStudyService.deleteWorkStudy(map);
					}
					sendJson.put("code", 1);
					sendJson.put("msg", "你删除勤工俭学信息成功啦！！");
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
			e.printStackTrace();
			sendJson.put("code", -1);
			sendJson.put("msg", "系统错误!");
		}

		logger.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "deleteWorkStudy");
	}
	
	
	/**
	 * 修改勤工俭学信息
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"id":"1","areaId":"10000","title":"深大图书馆","salary":"100","period":"1","negotiable":"2","recruitmentType":"1","telephoneNumber":"15338449700","adress":"深圳大学图书馆","positionDetails":"图书馆管理员，整理图书","recruitmentUnit":"深圳大学","nature":"1","scale":"1","userId":"255825"}}
	 * @param request
	 * @param response
	 */
	@RequestMapping("updateWorkStudy")
	public void updateWorkStudy(HttpServletRequest request,HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		logger.info("入口参数："+params);
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
					String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);
					Map<String,Object> map = new HashMap<>();
					map.put("id", contentStrJson.optString("id"));
					map.put("title", contentStrJson.optString("title"));
					map.put("salary", contentStrJson.optString("salary"));
					map.put("period", contentStrJson.optString("period"));
					map.put("negotiable", contentStrJson.optString("negotiable"));
					map.put("recruitmentType", contentStrJson.optString("recruitmentType"));
					map.put("telephoneNumber", contentStrJson.optString("telephoneNumber"));
					map.put("adress", contentStrJson.optString("adress"));
					map.put("positionDetails", contentStrJson.optString("positionDetails"));
					map.put("recruitmentUnit", contentStrJson.optString("recruitmentUnit"));
					map.put("nature", contentStrJson.optString("nature"));
					map.put("scale", contentStrJson.optString("scale"));
					map.put("areaId", contentStrJson.optString("areaId"));
					
					SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HH:mm:ss");
					Date date = new Date();
					String createTime = sdf.format(date);
					
					map.put("updateTime", createTime);
					map.put("userId", contentStrJson.optString("userId"));
					map.put("clickNum", contentStrJson.optString("clickNum"));

					//---调用service----
					int result = workStudyService.updateWorkStudy(map);
					//------------------
					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你修改勤工俭学信息成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你修改勤工俭学信息失败啦！！");
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
			e.printStackTrace();
			sendJson.put("code", -1);
			sendJson.put("msg", "系统错误!");
		}

		logger.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "updateWorkStudy");
	}
	
	
	/**
	 * 查询勤工俭学信息
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"recruitmentType":"1","tag":"软件工程","userId":"255825","areaId":"10000","id":"1","keyWord":"书","currentPage":"1","pageSize":"10"}}
	 * @param request
	 * @param response
	 */
	@RequestMapping("findWorkStudy")
	public void findWorkStudy(HttpServletRequest request,HttpServletResponse response) {
		System.out.println(System.currentTimeMillis());
		//创建一个JSON对象用来存储返回的数据
		JSONObject sendJson = new JSONObject();
		//获取参数
		String params = Encoder.getURLDecoderString(request.getParameter("params"));
		logger.info("入口参数："+params);
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
					String appVersion = paramsJson.optString("appVersion");
					String contentStr = paramsJson.optString("content");
					//将contentStr转成Json对象
					JSONObject contentStrJson = JSONObject.fromObject(contentStr);
					int currentPage = contentStrJson.optInt("currentPage");
					int pageSize = contentStrJson.optInt("pageSize");
					currentPage = (currentPage-1)*pageSize;
					
					
					
					Map<String,Object> map = new HashMap<>();
					map.put("areaId", contentStrJson.optString("areaId"));
					map.put("userId", contentStrJson.optString("userId"));
					map.put("keyWord", contentStrJson.optString("keyWord"));
					map.put("tag", contentStrJson.optString("tag"));
					map.put("recruitmentType", contentStrJson.optString("recruitmentType"));
					
					String id = contentStrJson.optString("id");
					if(!"".equals(id)) {
						Map<String, Object> map3 = new HashMap<>();
						map3.put("id", id);
						List<Map<String,Object>> list = workStudyService.findWorkStudy(map3);
						if(list.size() >0) {
							Map<String, Object> map2 = list.get(0);
							Object object = map2.get("clickNum");
							String clickNumStr = object.toString();
							int clickNum = Integer.parseInt(clickNumStr);
							int NewClickNum = clickNum + 1;
							map.put("clickNum", NewClickNum);
							//更新点击量
							map.put("id", id);
							workStudyService.updateWorkStudy(map);
							sendJson.put("code", 1);
							sendJson.put("list", list);
							sendJson.put("msg", "你查询勤工俭学信息成功啦！！");
						}else {
							sendJson.put("code", 1);
							sendJson.put("list", list);
							sendJson.put("msg", "你查询勤工俭学信息成功啦！！");
						}
					}else {
						//---调用service----
						List<Map<String,Object>> list2 = workStudyService.findWorkStudy(map);
						//------------------
						map.put("currentPage", currentPage);
						map.put("pageSize", pageSize);
						
						List<Map<String,Object>> list = workStudyService.findWorkStudy(map);
						
						sendJson.put("code", 1);
						sendJson.put("list", list);
						sendJson.put("SUM", list2.size());
						sendJson.put("msg", "你查询ap上的终端信息成功啦！！");

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
			e.printStackTrace();
			sendJson.put("code", -1);
			sendJson.put("msg", "系统错误!");
		}

		logger.info("返回给前端的内容为："+sendJson);
		PrintUtil.print(request, response, sendJson, "findWorkStudy");
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userId":"1234","itemId":"1"}}
	 * 用户添加收藏
	 */
	@RequestMapping("addCollectionForItem")
	public void addCollectionForItem(HttpServletRequest request,HttpServletResponse response) {
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


					//创建一个Map，将前端的数据封装成Map
					Map<String,Object> map = new HashMap<>();
					map.put("userId", contentStrJson.optString("userId"));
					map.put("itemId", contentStrJson.optString("itemId"));

					int result = workStudyService.addCollectionForItem(map);

					if(result == 1) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你添加收藏成功啦！！");
					}else {
						sendJson.put("code", 0);
						sendJson.put("msg", "你添加收藏失败啦！！");
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
		PrintUtil.print(request, response, sendJson, "addCollectionForItem");
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userId":"1234","itemId":"[3,5]"}}
	 * 用户删除收藏
	 */
	@RequestMapping("deleteCollectionForItem")
	public void deleteCollectionForItem(HttpServletRequest request,HttpServletResponse response) {
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

					String itemId = contentStrJson.optString("itemId");
					
					//创建一个Map，将前端的数据封装成Map
					Map<String,Object> map = new HashMap<>();
					map.put("userId", contentStrJson.optString("userId"));
					
					JSONArray array = JSONArray.fromObject(itemId);
					
					int result = 0;
					for (Object object : array) {
						map.put("itemId", object);
						workStudyService.deleteCollectionForItem(map);
					}
						sendJson.put("code", 1);
						sendJson.put("msg", "你删除收藏成功啦！！");
				
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
		PrintUtil.print(request, response, sendJson, "deleteCollectionForItem");
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userId":"1234"}}
	 * 通过用户ID，查找收藏夹
	 */
	@RequestMapping("findCollectionByUserId")
	public void findCollectionByUserId(HttpServletRequest request,HttpServletResponse response) {
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

					int userId = contentStrJson.optInt("userId");

					List<Map<String,Object>> list = workStudyService.findCollectionByUserId(userId);

					if(list != null && list.size() > 0 ) {
						sendJson.put("code", 1);
						sendJson.put("msg", "你通过用户ID查询收藏夹成功啦！！");
						sendJson.put("list", list);
					}else {
						sendJson.put("code", 1);
						sendJson.put("list", list);
						sendJson.put("msg", "你通过用户ID查询收藏夹为空!");
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
		PrintUtil.print(request, response, sendJson, "findCollectionByUserId");
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"status":"0"}}
	 * 通过状态查询广告
	 */
	@RequestMapping("findAd2")
	public void findAd2(HttpServletRequest request,HttpServletResponse response) {
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

					//(1/2;正常，下线)
					int status = contentStrJson.optInt("status");
					
					Map<String,Object> map = new HashMap<>();
					map.put("status", status);
					List<Map<String,Object>> list = workStudyService.findAd(map);

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
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"keyWord":"1","status":"1","currentPage":"1","pageSize":"10"}}
	 * 通过状态查询用户
	 */
	@RequestMapping("findUserByStatus")
	public void findUserByStatus(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.findUserByStatus(request, response);
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userId":"1","status":"1"}}
	 * 修改用户状态
	 */
	@RequestMapping("updateUserStatus")
	public void updateUserStatus(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.updateUserStatus(request, response);
	}
	
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"userId":"255825","userName":"takey","tel":"15338449700","areaId":"10000","status":"1","superManager":"0"}}
	 * 添加管理员
	 */
	@RequestMapping("addManager")
	public void addManager(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.addManager(request, response);
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"idList":"[2,3,4]"}}
	 * 删除管理员
	 */
	@RequestMapping("deleteManager")
	public void deleteManager(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.deleteManager(request, response);
	}
	
	
	/**
	 *  params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"id":"1","userId":"255825","userName":"takey","tel":"15338449700","areaId":"10000","status":"1","superManager":"0"}}
	 * 修改管理员
	 */
	@RequestMapping("updateManager")
	public void updateManager(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.updateManager(request, response);
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"currentPage":"1","pageSize":"10","areaId":"10000","status":"1","keyWord":"我"}}
	 * 查询管理员
	 */
	@RequestMapping("findManager")
	public void findManager(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.findManager(request, response);
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"adTitle":"aaa","adPhoto":"aaa","adClickLink":"aaa"}}
	 * 添加广告
	 */
	@RequestMapping("addAd")
	public void addAd(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.addAd(request, response);
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"idList":"[2,3,4]"}}
	 * 删除广告
	 */
	@RequestMapping("deleteAd")
	public void deleteAd(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.deleteAd(request, response);
	}
	
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"id":"1","status":"1","adTitle":"aaa","adPhoto":"aaa","adClickLink":"aaa"}}
	 * 修改广告
	 */
	@RequestMapping("updateAd")
	public void updateAd(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.updateAd(request, response);
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"status":"0"}}
	 * 查询广告
	 */
	@RequestMapping("findAd")
	public void findAd(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.findAd(request, response);
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"keyWord":"a、b、c"}}
	 * 添加或修改禁用文字
	 */
	@RequestMapping("addOrUpdateKeyWord")
	public void addOrUpdateKeyWord(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.addOrUpdateKeyWord(request, response);
	}
	
	/**
	 * params={"sign":"111","timeStamp":"111","appVersion":"3.0","content":{"keyWord":"0"}}
	 * 查询禁用文字
	 */
	@RequestMapping("findKeyWord")
	public void findKeyWord(HttpServletRequest request,HttpServletResponse response) {
		workStudyService.findKeyWord(request, response);
	}
}
