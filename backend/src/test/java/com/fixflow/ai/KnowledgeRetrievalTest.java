package com.fixflow.ai;

import com.fixflow.ai.dto.kb.KnowledgeArticleDto;
import com.fixflow.ai.model.KnowledgeArticle;
import com.fixflow.ai.repository.KnowledgeArticleRepository;
import com.fixflow.ai.service.KnowledgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KnowledgeRetrievalTest {

    @Mock
    private KnowledgeArticleRepository repo;

    @InjectMocks
    private KnowledgeService knowledgeService;

    @Test
    void keywordMatchingFindsRelevant() {
        KnowledgeArticle a = KnowledgeArticle.builder().id(1L).title("Reset Password").content("Use forgot password link to reset.").approved(true).build();
        when(repo.findByApprovedTrue()).thenReturn(List.of(a));
        List<KnowledgeArticleDto> res = knowledgeService.searchRelevant("cannot login reset password");
        assertFalse(res.isEmpty());
    }
}
