package com.junsheng.XiaozhiAI.config;

import com.junsheng.XiaozhiAI.store.MongoChatMemoryStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class XiaozhiAgentConfig {
    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Autowired
    private EmbeddingStore embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;
    @Bean
    public ChatMemoryProvider chatMemoryProviderXiaozhi(){
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(mongoChatMemoryStore)  // save memory in mongodb
                .build();
    }

//    @Bean
//    ContentRetriever contentRetrieverXiaozhi() {
//        Document document1 = FileSystemDocumentLoader.loadDocument("C:/Users/user/Documents/personal_develop/XiaozhiAI//XiaozhiAI/src/main/resources/RagResource.md");
//        List<Document> documents = Arrays.asList(document1);
//
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
//        return EmbeddingStoreContentRetriever.from(embeddingStore);
//
//    }

    @Bean
    ContentRetriever contentRetrieverXiaozhiPinecone() {
        return EmbeddingStoreContentRetriever
                .builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(1)
                .minScore(0.8)
                .build();
    }
}
