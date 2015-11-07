# Usage
This tool performs MA-based random sampling test on the input `Expression table` file. It outputs a `Expression score` file which consists of the significance of each tag.

# Input format
The input should be a TAB delimited text file of 2 or more columns. The first column is the labels of tags. Each of the remaining columns gives the expression quantities of all tags in each library.

# Output format
The output is a TAB delimited text file of 7 columns:
GeneNames   value1  value2  FoldChange(log)    FoldChange(normalized)   Significance(p-value/q-value) Signature

# References
1. Wang, L., Feng, Z., Wang, X., Wang, X. & Zhang, X. DEGseq: an R package for identifying differentially expressed genes from RNA-seq data. Bioinformatics (Oxford, England) 26, 136–8 (2010).