#!/bin/bash
# APK重组脚本
# 使用方法: cat apk_part_* > app-debug.apk

echo "重组APK文件..."
cat apk_part_aa apk_part_ab apk_part_ac apk_part_ad apk_part_ae apk_part_af > app-debug.apk
echo "完成！文件大小: $(du -h app-debug.apk | cut -f1)"
echo "MD5校验: $(md5sum app-debug.apk | cut -d' ' -f1)"
