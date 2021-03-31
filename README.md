### 基于朴素贝叶斯的文本情感分类

#### 文本分类流程

- 文本预处理

  - 去重：去除重复评论
  - 去损：对明显缺省和完全无法反映情感倾向性的数据进行过滤
  - 分词：利用 HalLP 对原始评论分词
  - 去除停用词：根据公开字典剔除中文预料中无法反映情感倾向性的词（我、的）

  ```
  # 示例：
  原始评论：今天是艳阳天！心情不错！（正面）
  处理结果：[今天/t, 艳阳天/n, 心情/n, 不错/a]
  存储形式：[今天, 艳阳天, 心情, 不错]
  ```

- 文本特征提取

  - 词袋模型：是个在自然语言处理和信息检索下被简化的表达模型。通过将句子分成词语的集合，依据不同的极性分别放到不同的词袋中，每个词袋表示一种类别。

    ```
    			   句子分词结果：c1,c2,c3,c4,c5,c6
    						   /    \
    						  /	     \
    						 /        \
    			   词袋1：c1,c3,c5  词袋2：c2,c4,c6
    ```

  - 特征权值：指特征词在文本语料集中的权重，文本分类领域常用的有 bool 权重、TF、TF-IDF 等，由于评论语料短小的特点，选择 bool 权重：
    $$
    bool(w_i) = \begin{cases} 1, & \text w_i \in d \\ 0, & \text w_i \notin d \end{cases}
    $$
    其中 $w_i \in d$ 表示词 $w_i$ 属于文本 $d$。

  <img src="https://note.youdao.com/yws/public/resource/30dbf02d1f596871f96b3bac4a78188f/xmlnote/3D48515EB85041E7A7B2615EF8FB33BE/8571" style="zoom: 60%;" />

- 特征提取与权值计算

  - 特征值计算：卡方验证（CHI）是一种用途广泛的假设检验方法。**卡方值**表示了**特征** **T** 与相关**类别** **X** 的关联程度，其值大小与受关联程度**成正比**，由于简单高效，广泛应用于文本分类领域。

  - 混淆矩阵示例：

    |                          | 正面评论 | 负面评论 |      |
    | :----------------------: | :------: | :------: | :--: |
    |  “喜欢“一词出现在评论中  |    A     |    B     | A+B  |
    | ”喜欢“一词未出现在评论中 |    C     |    D     | C+D  |
    |                          |   A+C    |   B+D    |  N   |

  - 卡方公式：
    $$
    x^2(t,c) = \frac {N * (AD-BC)^2} {(A+C)(B+D)(A+B)(C+D)}
    $$

  - 改进：
    $$
    x^2(t,c) = \frac {N * (AD-BC)^2} {(A+C)(B+D)(A+B)(C+D)} * arf * berta
    $$
    其中 $arf$ 为词 t 在文档 c 中出现的频次，$berta$ 为类内正确率，其定义为：$berta=\frac{A}{A+B}$

- 分类器选择

  <img src="https://note.youdao.com/yws/public/resource/30dbf02d1f596871f96b3bac4a78188f/xmlnote/B97AB38C9F1C47B1B1C96B59965F937D/8575" style="zoom:60%;" />

  - 贝叶斯定理：
    $$
    p(B_i|A) = \frac{p(B_i)(P(A|B_i))} {\sum^n_{j=1}Num(c_j)}
    $$

    - 先验概率：
      $$
      P(c_i)=\frac{Num(c_i)}{\sum^n_{j=1}Num(c_j)}
      $$
      即含有词 $c_j$ 的文档数除以总文档数

    - 后验概率：
      $$
      P(d_i|c_x)=\frac{Weight(d_i, c_x)} {\sum^n_{j=1}Weight(d_i,c_j)}
      $$
      即类 $c_x$ 中词 $d_i$ 的权重除以所有类中词 $d_i$ 的权重之和

    - 分类比较：
      $$
      C_i = \max\{P(c_j)\prod^n_{i=1}P(w_i,c_j)^{wt(w_i)} \}
      $$
      比较各类别概率，取大者，即为预测类别

    - 单个评论分类的具体计算过程：

      ![](https://note.youdao.com/yws/public/resource/30dbf02d1f596871f96b3bac4a78188f/xmlnote/74245EE858D147F5B717C4526F1F032F/8577)

    - 后验概率的平滑：若某个词既不在正面类也不在负面类中，此时，算得的后验概率便可能为0，将无法进行分类。采用**拉普拉斯平滑技术**：
      $$
      P(d_i|c_x)=\frac{Weight(d_i, c_x) + \delta} {\sum^n_{j=1}Weight(d_i,c_j) + V}
      $$
      其中 $\delta$ 在使用 bool 权值时取 1，$V=logZ$（$Z$ 为所有词的权值总和）

- 分类结果评估

  ```
  TP：正确预测为正面类；
  FP：错误的预测为正面类；
  FN：错误的预测为负面类；
  TN：正确的预测为负面类；
  ```

  - 4个指标：

    |     准确率（accuracy）      | 精确率（precision） |  召回率（recal）   |                 F值（F-measure）                  |
    | :-------------------------: | :-----------------: | :----------------: | :-----------------------------------------------: |
    | $\frac{TP+TN}{TP+FP+FN+TN}$ | $\frac{TP}{TP+FP}$  | $\frac{TP}{TP+FN}$ | $F_1=\frac{2*precision*recall}{precision+recall}$ |

    评价的时候，自然是希望Precision越高越好，同时Recall也越高越好，但事实上两者在某些情况下是有矛盾的。这样就需要综合考虑他们，最常见的方法是F-Measure，他是 P 和 R 的调和平均数。

  <img src="https://note.youdao.com/yws/public/resource/30dbf02d1f596871f96b3bac4a78188f/xmlnote/3D2337C7173E4158B699516B90758CFC/8579" style="zoom:60%;" />

---

#### 实现说明：

- 类别定义：

  ```
  0_pos:正面
  1_neg:负面
  ```

- 训练：

  使用词袋模型或向量空间模型对文本进行量化处理，便于后期计算；

  利用训练集计算分词权重并进行特征词挑选；

  利用贝叶斯算法根据权重计算分类后验概率；

- 测试：

  使用现有模型直接对测试集进行后验概率预测，并与真值进行比较；

- 效果：

  F1 值能达到 0.90，其余指标均超过 0.9，效果好的原因一个是二分类任务比较简答，一个是数据集质量比较高。

#### 代码说明

主要的4步处理见：[核心分类代码，按步骤](https://github.com/sinat-biao/weiboemotionclassing/tree/master/src/main/java/com/biao/weiboemotionclassing/operation)。

开发过程中所写过并调试过的所有函数和类都放进`test`目录下存档了，`src`目录下仅保留了最后确定和使用的代码。
