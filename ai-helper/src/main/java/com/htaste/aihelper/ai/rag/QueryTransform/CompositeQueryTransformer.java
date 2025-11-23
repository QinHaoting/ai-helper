package com.htaste.aihelper.ai.rag.QueryTransform;

import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CompositeQueryTransformer：可按顺序组合多个 QueryTransformer。
 * 先执行前一个，再把输出依次输入后一个，实现渐进增强。
 */
public class CompositeQueryTransformer implements QueryTransformer {

    private final List<QueryTransformer> transformers;

    public CompositeQueryTransformer(List<QueryTransformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public Collection<Query> transform(Query query) {
        Collection<Query> current = List.of(query);

        for (QueryTransformer transformer : transformers) {
            Collection<Query> next = new ArrayList<>();
            for (Query q : current) {
                Collection<Query> result = transformer.transform(q);
                next.addAll(result);
            }
            current = next;
        }

        return current;
    }
}
