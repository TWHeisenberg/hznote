#!/bin/bash 
 
# 批量删除hbase中的数据
# 执行下面的结果进行删除
# hbase shell delete_list 
 tablename=$1 
 
 startrow=$2 
 
 endrow=$3 
 
 delete_list=$4
 
 echo "scan '${tablename}',{STARTROW=>'${startrow}',ENDROW=>'${endrow}'}" |hbase shell|awk -F ' ' '{print $1'\t'}'> ./file.txt 
 
 #删除前6行非表中数据 
 
 sed -i '1,6d' file.txt 
 
 #删除最后一行（空行） 
 
 sed -i '$d' file.txt 
 
 #删除最后一行（总条数） 
 
 sed -i '$d' file.txt 
 
 cat ./file.txt|awk '{print $1}'|while read rowvalue 
 
 do 
 
  echo -e "deleteall '${tablename}','${rowvalue}'" >> ./tmp_rowkey_list 
  echo "${rowvalue}" 
 done 
 
 cat tmp_rowkey_list | sort -u > ${delete_list}
 
 # rm ./file.txt 
 
 echo "done"  
