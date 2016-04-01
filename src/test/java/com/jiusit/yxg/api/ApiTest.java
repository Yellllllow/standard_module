package com.jiusit.yxg.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webbuilder.utils.DateUtil;
import com.webbuilder.utils.StringUtil;
import com.zhiding.common.DataCoding;
import com.zhiding.common.HttpClientUtil;
import com.zhiding.common.JPushUtil;

import junit.framework.TestCase;
/**
 * API接口测试
 * @author LostVerge
 *
 */
public class ApiTest extends TestCase {
	private static final  Logger log  =  LoggerFactory.getLogger(ApiTest.class);
	
	//开发环境测试API地址
	private static final String HOST = "http://localhost:8091/yxg/api";
	//private static final String HOST = "http://121.40.89.144/yxg/api";
	//生产环境测试API地址
	//private static final String HOST = "http://222.77.181.25:8080/yxg/api";
	
	@Test
	/**
	 * 加解密测试
	 * @throws Exception  
	 */
	public void testDataCoding() throws Exception {
		Map<String, Object> data = new HashMap();
		data.put("device", "android");
		data.put("version", 25);
		data.put("command", "login");
		data.put("telephone", "yxl");  
		data.put("password", "123456");
		JSONObject json = new JSONObject(data);
		String sendString = DataCoding.encode(json.toString());
		log.info("encode:"+sendString);

		String recv = DataCoding.decode("1QTp//9iFzyjcJGsLuWq3rQ/i0/W1IKpvJMGq0CsDvDI3p3TDuaCcw==");
		log.info("decode - recv:"+recv);
		String rs = DataCoding.decode("1QTp//9iFzwaezrX57isV2yyaoKVH8rxL1qq0MernDqDAW5ErV4pZuc7BgGXErg9o1RKi1BRYpPcREFhIhX7iYSQQGsFne6dSnpH9y66bjnNMMLjUmo2eyOVRCk7Kp+vzZEy5up9VvbHR8sPDYbGoJSS/SW4BsiY8wFoTCBkNCP09HrbTP0y4a8StLXcnkmp18lkFF/bUpGVHFCl1eHzgev36fqVKNU8vWbBSiVvCq30DgOhgv1YiUvE3f/ABmRPZa1DfC35XAokse13kyG2yc7Ou5yHjH8LEaDha9rllKQij/j/f8yeh6kn5o/8aTwWA+OOMalZ+Boj/k6gtPicP38ZMocyS0sWR6/bGb3gp5sXlXZi6lKwghd6yweUzcWExAV7ucG9W+2i6mPW6ktj3w==");

		log.info("decode - rs:"+rs);
	}
	
	@Test
	/**
	 * 登录测试
	 */
	public void testLogin() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "login");
		Map<String, Object> data = new HashMap();
		data.put("wx_id", "23XCKO1LJ8RN");
		data.put("nick_name", "美男子");
		data.put("login_type", "1");
		data.put("telephone", "15980213543");
		data.put("password", "123456");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try { 
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testLogin error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 维修工注册发送
	 */
	public void testRegisterWorker() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "register");
		Map<String, Object> data = new HashMap();
		data.put("telephone", "15980213543");
		data.put("password", "123456");
		data.put("real_name", "郭颖彬");
		data.put("id_card", "3501014198912173316");
		data.put("recommended_id", "张三");
		data.put("bank_card", "1234567890");
		data.put("open_bank_name", "建设银行鼓楼支行");
		data.put("is_buy_safety", "0");
		
		data.put("service_procode", "11");
		data.put("service_citycode", "1101");
		data.put("service_countycode", "110101,110102");
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1,79572b51b1b540a68dfc5a9c897835a9");
		
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegister error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取报修类型
	 */
	@Test
	public void testGetRepairType() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getRepairType");
		Map<String, Object> data = new HashMap();
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 省市县
	 */
	public void testGetAreas() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getAreas");
		Map<String, Object> data = new HashMap();
		data.put("area_parent", "11");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 品牌
	 */
	public void testGetRepairBrand() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getRepairBrand");
		Map<String, Object> data = new HashMap();
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 故障原因
	 */
	public void testGetBrokenReason() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getBrokenReason");
		Map<String, Object> data = new HashMap();
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 备件信息
	 */
	public void testGetParts() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getParts");
		Map<String, Object> data = new HashMap();
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 提交个人维修单
	 */
	public void testSubmitPersonnelOrders() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "submitPersonnelOrders");
		
		Map<String, Object> data = new HashMap();
		
		data.put("pay_type", "01");//支付方式
		
		data.put("wx_id", "o192btyekNil4d"); //微信ID
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1");  //报修类型ID
		data.put("repair_type", "中央空调");//报修类型中文
		data.put("vip_code", "103432");//VIP码
		
		data.put("repair_brand", "格力");//品牌
		data.put("repair_model", "XTNT001");//型号
		data.put("repair_num", "1");//报修数量
		data.put("is_hurry", "1");//是否加急
		data.put("hurry_money", "40");
		
		data.put("broken_reason_id", "09ceec55fd4e46ca98208f73d8e9ffb4");//故障ID
		data.put("broken_reason", "无法制冷");//故障原因
		data.put("broken_desc", "空调无法制冷");//故障描述
		data.put("touch_people", "张三");
		data.put("touch_telephone", "123456789");
		data.put("is_replace", "0");
		data.put("report_people", "李四");
		data.put("report_telephone", "123456789");
		
		data.put("report_procode", "11");
		data.put("report_citycode", "1101");
		data.put("report_countycode", "110101");		
		data.put("repair_address", "福建省福州市晋安区岳峰镇");
		
		data.put("reserve_time", "2015-07-30 10:30");


		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 提交代报单
	 */
	public void testApplyInsteadOrder() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "applyInsteadOrder");
		Map<String, Object> data = new HashMap();
		
		data.put("pay_type", "02");//付款方式
		
		data.put("telephone", "15980213543"); //微信ID
		data.put("repair_type_id", "2f42e531fe1b47ff9fcecd6fafb04969");  //报修类型ID
		data.put("repair_type", "空调");//报修类型中文
		
		data.put("repair_brand", "格力");//品牌
		data.put("repair_model", "XTNT001");//型号
		data.put("repair_num", "1");//报修数量
		data.put("reserve_time", "2015-06-30 10:30");
		
		data.put("broken_reason_id", "377653dfeba14c2592db7a4b85ed17ec");//故障ID
		data.put("broken_reason", "无法启动");//故障原因
		data.put("broken_desc", "无法启动");//故障描述
		data.put("touch_people", "张三");
		data.put("touch_telephone", "123456789");
		
		data.put("report_procode", "11");
		data.put("report_citycode", "1101");
		data.put("report_countycode", "110101");
		
		data.put("repair_address", "福建省福州市晋安区岳峰镇");
		
		data.put("advance_repair_fee", "811.00");

		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 获取用户订单
	 */
	public void testGetPersonnelOrders() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getPersonnelOrders");
		Map<String, Object> data = new HashMap();
		data.put("repair_worker_tel", "15980213543");
		data.put("order_status", "08");
		
		//data.put("userId", "0bdb2a88d9084a60b1f2dd078f94dc76");
		//data.put("startDate", "2015-06-30");
		send.put("data",data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 抢单申请
	 */
	public void testRushOrder() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "rushOrder");
		Map<String, Object> data = new HashMap();
		data.put("repair_worker_tel", "15980213543");
		data.put("order_id", "56dea89415f04c75b76d8232a1f42cae");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 新增收费明细
	 */
	public void testInsertChargeDetail() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "insertChargeDetail");
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "edaadd56928e4559a5789a2b1e7288f7");
		data.put("repair_worker_tel", "15980213543");
		data.put("parts_id", "8b9f73ca5c6e4806b30df638db3261a9");
		data.put("parts_name", "主板");
		data.put("parts_price", "31.00");
		data.put("parts_num", "4");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 配件申请
	 */
	public void testApplyParts() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "applyParts");
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "edaadd56928e4559a5789a2b1e7288f7");
		data.put("repair_worker_tel", "15980213543");
		data.put("parts_id", "8b9f73ca5c6e4806b30df638db3261a9");
		data.put("parts_name", "主板");
		data.put("parts_price", "31.00");
		data.put("parts_num", "4");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 维修完成
	 */
	public void testRepairComplete() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "repairComplete");
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "56dea89415f04c75b76d8232a1f42cae");
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 确认维修完成
	 */
	public void testConfirmRepair() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "confirmRepair");
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "98d73385ce014006a89b4cf073d51508");
		data.put("isFinish","0"); 
		data.put("repair_score","5"); 
		data.put("repairDesc","不好"); 
		send.put("data",data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 维修工信息修改
	 */
	public void testUpdateWorkerInfo() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "updateWorkerInfo");
		Map<String, Object> data = new HashMap();
		data.put("telephone", "15980213543");
		data.put("real_name", "郭颖彬");
		data.put("id_card", "123456");
		data.put("bank_card", "111112222333");
		data.put("open_bank_name", "鼓楼支行"); 
		data.put("service_procode", "11");
		data.put("service_citycode", "1101");
		data.put("service_countycode", "110101,110102,110103");
		data.put("repair_type_id", "2c2c2731bd664d8fb018c97da22a23b1,79572b51b1b540a68dfc5a9c897835a9");
		data.put("is_buy_safety", "1");
		data.put("safety_begin_date", "2015-05-01");
		data.put("safety_end_date", "2017-05-01");
		send.put("data", data); 
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegister error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 新增收费明细
	 */
	public void testEstimateFeel() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "estimateFee");
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "56dea89415f04c75b76d8232a1f42cae");
		data.put("repair_worker_tel", "15980213543");
		data.put("repair_money", "200");
		
		List<Map<String,String>> partsList = new ArrayList<Map<String,String>>();
		
		Map<String,String> partsMap = new HashMap<String,String>();
		
		partsMap.put("parts_id", "d50403a36c6049f3aa3297cd0b1ebb52");
		partsMap.put("parts_name", "螺丝");
		partsMap.put("parts_price", "31.00");
		partsMap.put("parts_num", "4");
		partsMap.put("total_money", "124");
		partsMap.put("parts_desc", "描述");
		
		partsList.add(partsMap);
		
		//data.put("pay_arr", partsList);
		
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传极光推送ID
	 */
	@Test
	public void testPushID() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "putPushId");
		
		Map<String, Object> data = new HashMap();
		data.put("telephone", "18259136621");
		//data.put("news_id", "454d9024edb1406883c5b3d07b533816");
		data.put("reg_id", "01018cc79a6");
		data.put("device_type", "0");
		send.put("data", data);

		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());

		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			log.info("ret decode: "+DataCoding.decode(ret));
			assertNotNull(ret);
		} catch (Exception e) {
			log.error("DataCoding encode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 微信状态接口
	 */
	@Test
	public void testPushWx() {
		Map<String, Object> send = new HashMap();
		send.put("order_id", "f9ca75d0cb36487188451157deded114");
		send.put("order_state", "05");
		
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());

		try {
			String ret = HttpClientUtil.POSTMethod("http://w2yong77.vicp.cc/msg/personalOrderState", send, null);
			log.info("ret raw: "+ret);
			//log.info("ret decode: "+DataCoding.decode(ret));
			//assertNotNull(ret);
		} catch (Exception e) {
			log.error("DataCoding encode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付完成
	 */
	@Test
	public void testCompletePay() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "completePay");
		
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "98d73385ce014006a89b4cf073d51508");
		data.put("pay_type", "01");
		send.put("data", data);

		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());

		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			log.info("ret decode: "+DataCoding.decode(ret));
			assertNotNull(ret);
		} catch (Exception e) {
			log.error("DataCoding encode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取开票类型
	 */
	@Test
	public void testGetInvoiceInfo() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getInvoiceInfo");
		
		Map<String, Object> data = new HashMap();
		data.put("personnel_orders_id", "140b3e13f8bf40e29af293a2e33f1431");
		send.put("data", data);

		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());

		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			log.info("ret decode: "+DataCoding.decode(ret));
			assertNotNull(ret);
		} catch (Exception e) {
			log.error("DataCoding encode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * 测试消息推送工具类
	 */
	public void testJpush(){
		JPushUtil jpushUtil = new JPushUtil();
		
		String appKey = "e13d8ae2cc2ea088ec90eb7f";
		String masterSecret = "a661226f3ca992cb7bf8e8b1";
		String title = "测试";
		String content = "收到消息没";
		String[] reg_ids = {"050256d3818"};
		boolean succ = jpushUtil.sendMessageAll(appKey, masterSecret, true, title);
		//boolean succ = jpushUtil.sendMessageToAnd(appKey, masterSecret, false, title, content, reg_ids);
		System.out.println(succ);
	}
	
	@Test
	/**
	 * 确认维修完成
	 */
	public void testGetVouchers() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "getVouchers");
		Map<String, Object> data = new HashMap();
		data.put("wx_id", "23XK84WTHPU4");
		send.put("data",data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * 维修工附件上传
	 */
	public void testUploadWorkerFile() throws Exception {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "uploadWorkerFile");
		Map<String, Object> data = new HashMap();
		
		data.put("repair_worker_tel", "15980213543");
		data.put("file_name", "123.jpg");
		data.put("act_type", "01");
		File file2 = new File("D:\\123.jpg");
		InputStream in2 = new FileInputStream(file2);
		
		data.put("file",StringUtil.encodeBase64(in2));	
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: " + json.toString());
		try {
			// des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: " + sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST + "/cmd", argsMap, null);
			log.info("ret raw: " + ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: " + retas + "\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testGetPassword error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 提交团队维修单
	 */
	@Test
	public void testSubmitTeamOrders() {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "submitTeamOrders");
		Map<String, Object> data = new HashMap();
		
		data.put("wx_id", "o192btyekNil4d1MNigZZ2w3zaPw"); //微信ID
		data.put("company_name","");//公司名称
		
		data.put("touch_people", "张三");
		data.put("touch_telephone", "123456789");
		
		data.put("report_procode", "11");
		data.put("report_citycode", "1101");
		data.put("report_countycode", "110101");
		data.put("repair_address", "福建省福州市晋安区岳峰镇");
		
		data.put("is_hurry", "0");//是否加急
		
		data.put("reserve_time", "2015-06-30 10:30");
		data.put("crash_fee", "50");
		
		List<Map<String,String>> taskList = new ArrayList<Map<String,String>>();
		
		Map<String,String> taskMap = new HashMap<String,String>();		
		
		taskMap.put("repair_type_id", "2f42e531fe1b47ff9fcecd6fafb04969");  //报修类型ID
		taskMap.put("repair_type", "空调");//报修类型中文		
		taskMap.put("repair_brand", "格力");//品牌
		taskMap.put("repair_model", "XTNT001");//型号
		taskMap.put("repair_num", "15");//报修数量		
		taskMap.put("broken_reason_id", "067b88f22d0b42d1912863e5662ef038");//故障ID
		taskMap.put("broken_reason", "冷冻管破裂");//故障原因
		taskMap.put("broken_desc", "空调无法制冷");//故障描述

		taskList.add(taskMap);
		
		taskMap = new HashMap<String,String>();		
		
		taskMap.put("repair_type_id", "5ee22b6d4bf04aa8879e81bc9d13048c");  //报修类型ID
		taskMap.put("repair_type", "电脑");//报修类型中文		
		taskMap.put("repair_brand", "联想");//品牌
		taskMap.put("repair_model", "Y430p");//型号
		taskMap.put("repair_num", "10");//报修数量		
		taskMap.put("broken_reason_id", "broken_reason_id");//故障ID
		taskMap.put("broken_reason", "主板损坏");//故障原因
		taskMap.put("broken_desc", "电脑蓝屏");//故障描述

		taskList.add(taskMap);
		
		data.put("task",taskList);
		

		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: "+json.toString());
		try {
			//des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: "+sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
			log.info("ret raw: "+ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: "+retas+"\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testRegisterVerificationCode error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	@Test
	/**
	 * 维修工附件上传
	 */
	public void testApplyAfterSales() throws Exception {
		Map<String, Object> send = new HashMap();
		send.put("device", "android");
		send.put("version", 25);
		send.put("command", "applyAfterSales");
		Map<String, Object> data = new HashMap();
		
		data.put("personnel_orders_id", "56dea89415f04c75b76d8232a1f42cae");
		data.put("apply_reason", "坏了");

		
		send.put("data", data);
		JSONObject json = new JSONObject(send);
		log.info("json: " + json.toString());
		try {
			// des加密在发送
			String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
			log.info("send: " + sendString);
			Map<String, Object> argsMap = new HashMap();
			argsMap.put("data", sendString);
			String ret = HttpClientUtil.POSTMethod(HOST + "/cmd", argsMap, null);
			log.info("ret raw: " + ret);
			String retas = DataCoding.decode(ret);
			log.info("ret decode: " + retas + "\n");
			JSONObject retJson = new JSONObject(retas);
			assertEquals(0, retJson.get("code"));
		} catch (Exception e) {
			log.error("testGetPassword error " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	 /**
	  * 获取新闻列表
	  */
	 public void testNewsList() {
	  Map<String, Object> send = new HashMap();
	  send.put("device", "android");
	  send.put("version", 25);
	  send.put("command", "newsList");
	  Map<String, Object> data = new HashMap();
	  data.put("news_type", "01");
	  data.put("page", "1");
	  data.put("limit", "2");
	  send.put("data", data);
	  JSONObject json = new JSONObject(send);
	  log.info("json: " + json.toString());
	  try {
	   // des加密在发送
	   String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
	   log.info("send: " + sendString);
	   Map<String, Object> argsMap = new HashMap();
	   argsMap.put("data", sendString);
	   String ret = HttpClientUtil.POSTMethod(HOST + "/cmd", argsMap, null);
	   log.info("ret raw: " + ret);
	   String retas = DataCoding.decode(ret);
	   log.info("ret decode: " + retas + "\n");
	   JSONObject retJson = new JSONObject(retas);
	   assertEquals(0, retJson.get("code"));
	  } catch (Exception e) {
	   log.error("error " + e.getMessage());
	   e.printStackTrace();
	  }
	 }

	/*
	  * 获取新闻首页
	  * */
	 @Test
	 public void testrecommended() {
	  Map<String, Object> send = new HashMap<String, Object>();
	  send.put("device", "android");
	  send.put("version", 25);
	  send.put("command", "recommended");
	  Map<String, Object> data = new HashMap();
	  data.put("page", "1");
	  data.put("limit", "1");
	  send.put("data", data);
	  JSONObject json = new JSONObject(send);
	  log.info("json: "+json.toString());
	  try {
	   //des加密在发送
	   String sendString = DataCoding.encode(json.toString(), "XcKi0k89");
	   log.info("send: "+sendString);
	   Map<String, Object> argsMap = new HashMap();
	   argsMap.put("data", sendString);
	   String ret = HttpClientUtil.POSTMethod(HOST+"/cmd", argsMap, null);
	   log.info("ret raw: "+ret);
	   String retas = DataCoding.decode(ret);
	   log.info("ret decode: "+retas+"\n");
	   JSONObject retJson = new JSONObject(retas);
	   assertEquals(0, retJson.get("code"));
	  } catch (Exception e) {
	   log.error("testRecommended error " + e.getMessage());
	   e.printStackTrace();
	  }
	 }
}
