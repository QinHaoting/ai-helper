# AI Helper (物流客服助手)

AI Helper 是一个基于 Java Spring Boot 和 LangChain4j 构建的智能 AI 助手项目，旨在提供高效的物流客服解决方案。项目集成了先进的 RAG（检索增强生成）技术、多模态交互能力以及灵活的对话记忆管理，能够处理复杂的业务咨询并提供准确的响应。

## 🚀 核心功能

### 1. RAG (检索增强生成) 系统
本项目实现了完整的 RAG 流程，能够基于本地文档库回答特定领域的业务问题（如订单处理、退换货政策等）。

*   **文档摄入 (Document Ingestion)**:
    *   支持多种格式文档解析（基于 Apache Tika）。
    *   **智能切分**: 使用 `DocumentByParagraphSplitter` 按段落进行文档切分（最大 1000 字符，重叠 200 字符），保证上下文完整性。
    *   **元数据增强**: 在切片中自动注入文件名等元数据，提升检索精准度。
    *   **向量化存储**: 自动将文档切片向量化并存入 Embedding Store。

*   **查询优化 (Query Transformation)**:
    *   实现了 `SmartQueryTransformer`，结合了查询重写和多语句扩展。
    *   **多语句扩展**: 默认将用户查询扩展为 3 个相关查询，提高检索覆盖率。

*   **检索增强 (Retrieval Augmentation)**:
    *   使用 `EmbeddingStoreContentRetriever` 进行基于向量相似度的内容检索。
    *   默认返回 Top 5 最相关的文档片段。
    *   支持未来扩展重排（Re-ranking）机制。

### 2. 多模态与智能对话
*   **多模态交互**: 支持图片理解与问答 (`chatWithImage`)。
*   **流式响应**: 支持 Server-Sent Events (SSE) 流式输出 (`chatWithStream`)，提供实时的打字机效果体验。
*   **记忆管理**:
    *   **上下文记忆**: 支持基于会话 ID 的完整历史记录记忆。
    *   **摘要记忆**: 实现了 `MessageConversationSummaryMemory`，能够自动总结长对话，在保持上下文的同时节省 Token。

## 🛠 技术栈

*   **后端**: Java, Spring Boot
*   **AI 框架**: LangChain4j
*   **文档解析**: Apache Tika
*   **存储**: MongoDB (用于向量存储和聊天记录)
*   **前端**: Vue 3, Vite, Element Plus

## 📂 项目结构

```text
ai-helper-project/
├── README.md
├── ai-helper/                  # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/htaste/aihelper/
│           │   ├── AiHelperApplication.java
│           │   └── ai/
│           │       ├── AIHelperService.java      # AI 服务接口
│           │       ├── controller/               # API 控制器
│           │       ├── rag/                      # RAG 核心模块
│           │       │   ├── DocumentIngestionService.java # 文档摄入
│           │       │   ├── RagConfig.java        # RAG 配置
│           │       │   └── QueryTransform/       # 查询优化
│           │       ├── memory/                   # 记忆模块
│           │       └── store/                    # 向量存储配置
│           └── resources/
│               ├── application.yml
│               ├── docs/                         # 本地知识库文档
│               └── static/                       # System Prompts
└── ai-helper-frontend/         # 前端 Vue 3 项目
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── App.vue
        ├── main.js
        └── components/
```

## ⚡ 快速启动

### 后端启动 (ai-helper)

1.  **环境准备**: 确保本地已安装 JDK 17+, Maven, MongoDB。
2.  **配置**: 修改 `ai-helper/src/main/resources/application.yml`，配置 AI 模型密钥和 MongoDB 连接信息。
3.  **运行**:
    ```bash
    cd ai-helper
    mvn spring-boot:run
    ```
4.  **初始化知识库**: 调用接口 `POST /api/rag/ingest?path=src/main/resources/docs` 初始化 RAG 索引。

### 前端启动 (ai-helper-frontend)

1.  **环境准备**: 确保本地已安装 Node.js (推荐 LTS 版本)。
2.  **安装依赖**:
    ```bash
    cd ai-helper-frontend
    npm install
    ```
3.  **运行**:
    ```bash
    npm run dev
    ```
4.  **访问**: 打开浏览器访问 `http://localhost:5173` (默认端口)。
