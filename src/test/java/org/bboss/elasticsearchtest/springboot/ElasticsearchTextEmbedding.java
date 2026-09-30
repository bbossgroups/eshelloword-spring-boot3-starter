package org.bboss.elasticsearchtest.springboot;
/**
 * Copyright 2025 bboss
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

import org.bboss.elasticsearchtest.springboot.embedding.ElasticsearchEmbeddingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author biaoping.yin
 * @Date 2025/5/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTextEmbedding {
	@Autowired
	private ElasticsearchEmbeddingService elasticsearchEmbeddingService;
    @Test
    public void testEmbedding(){
        //Elasticsearch KNN search参考文档：https://www.elastic.co/docs/solutions/search/vector/knn#knn-search-filter-example
        elasticsearchEmbeddingService.testCreateTextEmbeddingIndex();
        elasticsearchEmbeddingService.bulkdata();
//        elasticsearchEmbeddingService.search();
//        elasticsearchEmbeddingService.search1();
//        elasticsearchEmbeddingService.searchWithFilter();
//        elasticsearchEmbeddingService.searchWithScore();
        elasticsearchEmbeddingService.searchVectorAndRerank();
    }

    @Test
    public void testEmbedding1(){
        //Elasticsearch KNN search参考文档：https://www.elastic.co/docs/solutions/search/vector/knn#knn-search-filter-example
        elasticsearchEmbeddingService.searchVectorAndRerank();
    }
    
   
     
}
