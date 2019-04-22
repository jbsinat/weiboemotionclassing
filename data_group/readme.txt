最后确定的项目中各个过程使用的文件有：
原始语料集：
data_group/initial_data_set/0_simplifyweibo_huanyuanhou.txt
data_group/initial_data_set/1_simplifyweibo_huanyuanhou.txt

从原始语料集提取的用于训练的训练语料集（每一类各1000条数据）：
data_group/simple_data_set/0_1000_comments.txt
data_group/simple_data_set/1_1000_comments.txt

特征选取过程：
    存放的文本预处理结果文件（分词、去重、去除停用词等）：
    data_group/segmented_data_set/0_pos_fencihou.txt
    data_group/segmented_data_set/1_neg_fencihou.txt

    以键值对形式存放的 词+CHI值 文件(已经通过阈值进行过滤了)：
    data_group/feature_word_set/0_pos_with_CHI.txt
    data_group/feature_word_set/1_neg_with_CHI.txt

    bool权重计算后生成的以键值对形式存放的 词+bool权值 文件（对阈值过滤之后留下的词进行bool权值计算）：
    data_group/feature_word_set/0_pos_all_with_boolQZ.txt
    data_group/feature_word_set/1_neg_all_with_boolQZ.txt

