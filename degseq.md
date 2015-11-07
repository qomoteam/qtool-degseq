# Usage

It calculates a p-value for each gene. Input format is BED and output format is CSV.
Each row of the input file is a gene. Each column is a library. Columns should be divided into two groups.
For example, if the `groups` parameter is `{1,2,3},{4,5,6}` then 1,2,3 columns compose the 1st sample and 4,5,6 the 2nd. 

## Input format

The input should be a TAB delimited CSV file of 3 or more columns. The first column give the labels of genes. Each of the remaining columns gives the counts of reads in each library.

## Output format

The output is a TAB delimited CSV file with the following column headers:

"GeneNames"	"value1"	"value2"	"log2(Fold_change)"	"log2(Fold_change) normalized"	"p-value"

"GeneNames" is the same as the first column of the input. "value1(2)" is the count of reads of group 1(2).
 
# Reference

1. Wang, L., Feng, Z., Wang, X., Wang, X. & Zhang, X. DEGseq: an R package for identifying differentially expressed genes from RNA-seq data. Bioinformatics (Oxford, England) 26, 136–8 (2010).
