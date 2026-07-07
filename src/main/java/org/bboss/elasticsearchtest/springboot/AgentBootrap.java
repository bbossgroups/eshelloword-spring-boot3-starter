package org.bboss.elasticsearchtest.springboot;
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

import jakarta.annotation.PostConstruct;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author biaoping.yin
 * @Date 2026/6/2
 */
 
@Component 
public class AgentBootrap {
	private static Logger log = LoggerFactory.getLogger(AgentBootrap.class);

	@PostConstruct
	public void start(){
 
		log.info("初始化大模型maas服务");
		HttpRequestProxy.startHttpPools("maas.properties");
 
		
		log.info("AgentBootrap start");
		
		
	}
	 
	
	
}
