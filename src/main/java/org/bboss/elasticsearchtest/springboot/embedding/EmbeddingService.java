package org.bboss.elasticsearchtest.springboot.embedding;
/**
 * Copyright 2026 bboss
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

import com.example.esbboss.entity.Demo;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.MetaMap;
import org.frameworkset.spi.ai.AIAgent;
import org.frameworkset.spi.ai.model.AIRuntimeException;
import org.frameworkset.spi.ai.model.EmbeddingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @author biaoping.yin
 * @Date 2026/7/7
 */
@Service
@DependsOn("agentBootrap")
public class EmbeddingService {
	
	private static Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
	@Autowired
	private BBossESStarter bbossESStarter;
	
	/**
	 * 创建向量索引表结构
	 */
	public void createEmbeddingIndice() {
		//获取默认的rest客户端,加载创建向量表的dsl配置文件
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/easysearch-dsl.xml");
		if(clientUtil.existIndice("embedding-test")) {
			clientUtil.dropIndice("embedding-test");
			
		}
		String result  = clientUtil.createIndiceMapping(	"embedding-test","createEmbeddingIndice");
		logger.info("createEmbeddingIndice success,result:{}",result);
	}
	
	/**
	 * 文本向量化
	 * @param text
	 * @return
	 */
	public float[] text2embedding(String text) {
		EmbeddingMessage embeddingMessage = new EmbeddingMessage();
		embeddingMessage.setInput(text);          // 设置将要向量化的数据
		embeddingMessage.setModel("bge-m3");      // 指定向量模型名称
		embeddingMessage.setMaas("embedding_model"); // 指定 MaaS 平台服务名
		embeddingMessage.setRetry(3);
		embeddingMessage.setRetryInterval(500L);
		
		AIAgent agent = new AIAgent();
		float[] embedding = agent.embedding(embeddingMessage);
		if (embedding == null) {
			throw new AIRuntimeException("Embedding failed: response is null");
		}
		return embedding;
	}
	
	/**
	 * 创建向量索引表结构
	 */
	public void addEmbeddingDocuments() {
		//获取默认的rest客户端
		ClientInterface clientUtil = bbossESStarter.getRestClient();
		ClientOptions clientOptions = new ClientOptions();
		clientOptions.setIdField("id");//通过clientOptions指定map中的key为id的字段值作为文档_id，
		clientOptions.setPersistMapDocId(false);//id作为文档标识后，不作为记录字段存储 
		clientOptions.setRefreshOption("refresh=true");//为了测试效果,启用强制刷新机制，实际线上环境去掉最后一个参数"refresh=true"，线上环境谨慎设置这个参数
		List<Map> datas = new ArrayList<>();
		 
		Map<String,Object> map = new LinkedHashMap<>();
		map.put("id","0");
		map.put("title","Easysearch 分布式搜索引擎");
		String content = "Easysearch是基于Apache Lucene的国产分布式搜索型数据库，兼容Elasticsearch 7.x API。其原理核心是：通过分片将数据分布到集群节点，由主分片处理写入、副本分片保障高可用并提升读吞吐。" +
				"写入遵循“先主后副”确保一致，搜索采用Query-Fetch两阶段先找候选再取回详情，并利用倒排索引实现高性能全文检索与聚合分析";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		map = new LinkedHashMap<>();
		map.put("id","01");
		map.put("title","Elasticsearch 分布式搜索引擎");
		content = "Elasticsearch基于Apache Lucene构建，采用倒排索引实现高速全文检索。其分布式核心为：将索引切分为多个分片分布存储，" +
				"通过主分片处理写入、副本分片提升读性能与容灾。写入遵循“先主后副”确保一致性，" +
				"搜索时执行Query-Then-Fetch两阶段，先广播查询获取全局排序，再回原节点取回完整数据。";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		map = new LinkedHashMap<>();
		map.put("id","02");
		map.put("title","Opensearch 分布式搜索引擎");
		content = "OpenSearch是一款基于Apache Lucene构建的分布式搜索与分析引擎。其核心架构由集群、节点、索引和分片构成。" +
				"它通过将索引切分为主分片与副本分片，" +
				"分布存储于集群节点中，以实现数据高可用与读写负载均衡。依托倒排索引实现高性能全文检索，并提供REST API进行交互";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		map = new LinkedHashMap<>();
		map.put("id","03");
		map.put("title","深度学习原理介绍");
		content = "深度学习是机器学习的分支，核心是构建含多个隐层的人工神经网络。原理上，" +
				"通过前向传播计算输入数据在各层神经元中的加权和与非线性激活，得到预测输出。利用损失函数衡量预测与真实值的差距，" +
				"再通过反向传播算法逐层计算梯度，并采用梯度下降等优化器更新网络权重，以最小化损失，" +
				"从而让模型自动从海量数据中学习复杂特征与模式。";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		map = new LinkedHashMap<>();
		map.put("id","2");
		map.put("title","向量相似度搜索原理");
		content = "向量相似度搜索将非结构化数据（文本、图像等）转化为高维空间中的浮点向量。原理是在该空间内通过距离度量（如余弦相似度、欧氏距离）衡量向量间的邻近程度，" +
				"以此代表语义关联。为应对海量数据，常采用近似最近邻（ANN）算法（如HNSW、IVF）构建索引，在牺牲极小精度下大幅提升检索速度，实现毫秒级的语义匹配。";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		map = new LinkedHashMap<>();
		map.put("id","3");
		map.put("title","linux操作系统原理");
		content = "Linux以内核为核心，采用宏内核架构，但兼具模块化特性。其基本原理是时间片轮转实现多任务，分页存储管理内存。它通过虚拟文件系统抽象硬件差异，" +
				"以进程间通信及调度算法为核心驱动力。秉承“一切皆文件”哲学，通过分层设计将硬件、内核与用户空间隔离，在高效利用资源的同时确保系统稳健与安全。";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		
		
		map = new LinkedHashMap<>();
		map.put("id","4");
		map.put("title","bboss 分布式微服务框架");
		content = "bboss http5,一个简单而功能强大的、基于httpclient5的、去中心化的http/https负载均衡器、http rpc框架以及java ai客户端;基于http/https协议实现的客户端-服务端点到点的负载均衡和集群容灾功能，可以基于post/get/put/requestbody等方法对接调用任何基于http协议开发的微服务，包括spring cloud、spring boot、spring mvc以及其他基于http协议开发的微服务；提供streamchat方法，轻松对接各种大模型服务，" +
				"实现流式对话应用；完全支持http2协议；同时还可以非常方便地实现多个文件上传服务器。可基于apollo和nacos管理服务配置参数和实现服务发现功能。";
		map.put("content",content);
		map.put("embedding",text2embedding(content));
		datas.add(map);
		clientUtil.addDocuments("embedding-test",datas,clientOptions);
	}
	
	/**
	 * 创建向量索引表结构
	 */
	public void embeddingDocumentsSearch() {
		//获取默认的rest客户端,加载向量检索的dsl配置文件
		ClientInterface clientUtil = bbossESStarter.getConfigRestClient("esmapper/easysearch-dsl.xml");
		Map params = new HashMap<>();
		params.put("title","原理");
		params.put("content",text2embedding("相似度"));
		params.put("size",10);
		ESDatas<MetaMap> esDatas = clientUtil.searchList("embedding-test/_search","embeddingDocumentsSearch",params, MetaMap.class);
//		logger.info("esDatas:{}",esDatas);
		if(esDatas != null && esDatas.getDatas() != null) {
			for (MetaMap data : esDatas.getDatas()) {
				logger.info("id:{},title:{},score:{},content:{}", data.getId(),
						data.get("title"), data.getScore(), data.get("content"));
				
			}
		}
	}
}


