package org.bboss.elasticsearchtest.springboot;
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

import org.bboss.elasticsearchtest.springboot.bulk.TestBulkProcessor7x;
import org.bboss.elasticsearchtest.springboot.embedding.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.lang.Thread.sleep;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/9/18 10:27
 * @author biaoping.yin
 * @version 1.0
 */
@SpringBootTest
public class EmbeddingServiceTest {
	private Logger logger = LoggerFactory.getLogger(EmbeddingServiceTest.class);
 
	@Autowired
	private EmbeddingService embeddingService;
	@Test
	public void testEmbeddingService(){
		
		embeddingService.createEmbeddingIndice();
		
		embeddingService.addEmbeddingDocuments();
		
		embeddingService.embeddingDocumentsSearch();
		 
	}
}
