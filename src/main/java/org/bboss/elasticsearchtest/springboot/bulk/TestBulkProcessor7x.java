package org.bboss.elasticsearchtest.springboot.bulk;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.bulk.BulkCommand;
import org.frameworkset.elasticsearch.bulk.BulkInterceptor;
import org.frameworkset.elasticsearch.bulk.BulkProcessor;
import org.frameworkset.elasticsearch.bulk.BulkProcessorBuilder;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: for elasticsearch 7x</p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/12/8 9:57
 * @author biaoping.yin
 * @version 1.0
 */
@Service
public class TestBulkProcessor7x {
	private static Logger logger = LoggerFactory.getLogger(TestBulkProcessor7x.class);
	@Autowired
	private BBossESStarter bbossESStarter;
 
	/**
	 * BulkProcessor批处理组件，一般作为单实例使用，单实例多线程安全，可放心使用
	 */
	private BulkProcessor bulkProcessor;
	private Object lock = new Object();
	public void buildBulkProcessor(){
		if(bulkProcessor != null)
			return;
		synchronized (lock){
			if(bulkProcessor != null)
				return;
			ClientInterface clientInterface = bbossESStarter.getRestClient();
			//清除索引bulkdemo
			if(clientInterface.existIndice("bulkdemo")) {
				clientInterface.dropIndice("bulkdemo");
				logger.info("drop index bulkdemo success");
			}
			//定义BulkProcessor批处理组件构建器
			BulkProcessorBuilder bulkProcessorBuilder = new BulkProcessorBuilder();
			bulkProcessorBuilder.setBlockedWaitTimeout(0)//指定bulk数据缓冲队列已满时后续添加的bulk数据排队等待时间，如果超过指定的时候数据将被拒绝处理，单位：毫秒，默认为0，不拒绝并一直等待成功为止
					.setBulkSizes(50)//按批处理数据记录数
					.setFlushInterval(5000)//强制bulk操作时间，单位毫秒，如果自上次bulk操作flushInterval毫秒后，数据量没有满足BulkSizes对应的记录数，但是有记录，那么强制进行bulk处理
					
					.setWarnMultsRejects(1000)//bulk处理操作被每被拒绝WarnMultsRejects次（1000次），在日志文件中输出拒绝告警信息
					.setWorkThreads(5)//bulk处理工作线程数
					.setWorkThreadQueue(1)//bulk处理工作线程池缓冲队列大小
					.setBulkProcessorName("test_bulkprocessor")//工作线程名称，实际名称为BulkProcessorName-+线程编号
					.setBulkRejectMessage("Reject test bulkprocessor")//bulk处理操作被每被拒绝WarnMultsRejects次（1000次），在日志文件中输出拒绝告警信息提示前缀
//				.setElasticsearch("default")//指定Elasticsearch集群数据源名称，bboss可以支持多数据源
					.addBulkInterceptor(new BulkInterceptor() {
						public void beforeBulk(BulkCommand bulkCommand) {
							logger.debug("beforeBulk");
						}
						
						public void afterBulk(BulkCommand bulkCommand, String result) {
							if(logger.isDebugEnabled())
								logger.debug("afterBulk："+result);
//						System.out.println("totalSize:"+bulkCommand.getTotalSize());
//						System.out.println("totalFailedSize:"+bulkCommand.getTotalFailedSize());
						}
						
						public void exceptionBulk(BulkCommand bulkCommand, Throwable exception) {
							logger.info("exceptionBulk：",exception);
						}
						public void errorBulk(BulkCommand bulkCommand, String result) {
							logger.info("errorBulk："+result);
						}
					})
			// https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html
			//下面的参数都是bulk url请求的参数：RefreshOption和其他参数只能二选一，配置了RefreshOption（类似于refresh=true&&aaaa=bb&cc=dd&zz=ee这种形式，将相关参数拼接成合法的url参数格式）就不能配置其他参数，
			// 其中的refresh参数控制bulk操作结果强制refresh入elasticsearch，便于实时查看数据，测试环境可以打开，生产不要设置
//				.setRefreshOption("refresh")
//				.setTimeout("100s")
//				.setMasterTimeout("50s")
//				.setRefresh("true")
//				.setWaitForActiveShards(2)
//				.setRouting("1") //(Optional, string) Target the specified primary shard.
//				.setPipeline("1") // (Optional, string) ID of the pipeline to use to preprocess incoming documents.
			;
			/**
			 * 构建BulkProcessor批处理组件，一般作为单实例使用，单实例多线程安全，可放心使用
			 */
			bulkProcessor = bulkProcessorBuilder.build();//构建批处理作业组件
		}
		
	}
	public void testBulkDatas(){
		logger.info("testBulkDatas");
		ClientOptions clientOptions = new ClientOptions();
		clientOptions.setIdField("id");//通过clientOptions指定map中的key为id的字段值作为文档_id，
		clientOptions.setPersistMapDocId(false);//id作为文档标识后，不作为记录字段存储
		;
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("name","duoduo1");
		data.put("id",1);
		bulkProcessor.insertData("bulkdemo",data,clientOptions);
		data = new HashMap<String,Object>();
		data.put("name","duoduo2");
		data.put("id",2);
		bulkProcessor.insertData("bulkdemo",data,clientOptions);
		data = new HashMap<String,Object>();
		data.put("name","duoduo3");
		data.put("id",3);
		bulkProcessor.insertData("bulkdemo",data,clientOptions);
		data = new HashMap<String,Object>();
		data.put("name","duoduo4");
		data.put("id",4);
		bulkProcessor.insertData("bulkdemo",data,clientOptions);
		data = new HashMap<String,Object>();
		data.put("name","duoduo5");
		data.put("id",5);

		bulkProcessor.insertData("bulkdemo",data,clientOptions);
	 
		List<Object> datas = new ArrayList<Object>();
		for(int i = 6; i < 20; i ++) {
			data = new HashMap<String,Object>();
			data.put("name","duoduo"+i);
			data.put("id",i);
			datas.add(data);
		}
		bulkProcessor.insertDatas("bulkdemo",datas,clientOptions);
	 

	}
	
	public void shutdown(boolean asyn) {
		if(asyn) {
			Thread t = new Thread() {
				public void run() {
						bulkProcessor.shutDown();
					logger.info("bulkProcessor.shutDown() success");
				}
			};
			t.start();
		}
		else {
			logger.info("bulkProcessor.shutDown() begin.");
			bulkProcessor.shutDown();
				logger.info("bulkProcessor.shutDown() success");
		}


//		System.out.println("bulkProcessor.getTotalSize():"+bulkProcessor.getTotalSize());
	}

}
